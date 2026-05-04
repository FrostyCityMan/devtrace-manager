package com.devtrace.manager.vcs.parser;

import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.vcs.dto.VcsChangeFileEntity;
import com.devtrace.manager.vcs.dto.VcsChangeLogEntity;
import com.devtrace.manager.vcs.dto.VcsType;
import java.io.StringReader;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Component
public class SvnLogParser implements VcsLogParser {

    @Override
    public List<VcsChangeLogEntity> parse(UUID projectId, String logText) {
        if (logText == null || logText.isBlank()) {
            throw new BusinessException("SVN 로그 텍스트는 필수입니다.", "SVN_LOG_REQUIRED");
        }
        if (!logText.trim().startsWith("<")) {
            throw new BusinessException("SVN XML 로그만 지원합니다.", "SVN_LOG_XML_REQUIRED");
        }
        List<VcsChangeLogEntity> logs = parseXml(projectId, logText);
        if (logs.isEmpty()) {
            throw new BusinessException("파싱 가능한 SVN 로그가 없습니다.", "SVN_LOG_PARSE_EMPTY");
        }
        return logs;
    }

    private List<VcsChangeLogEntity> parseXml(UUID projectId, String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
            NodeList entries = document.getElementsByTagName("logentry");
            List<VcsChangeLogEntity> logs = new ArrayList<>();
            for (int i = 0; i < entries.getLength(); i++) {
                Element entry = (Element) entries.item(i);
                String message = text(entry, "msg");
                VcsChangeLogEntity log = new VcsChangeLogEntity();
                log.setProjectId(projectId);
                log.setVcsType(VcsType.SVN);
                log.setRevisionNo(entry.getAttribute("revision"));
                log.setAuthor(text(entry, "author"));
                log.setChangedAt(OffsetDateTime.parse(text(entry, "date")).toLocalDateTime());
                log.setMessage(message);
                log.setIssueKeys(IssueKeyMatcher.extractIssueKeys(message));
                log.setChangedFiles(parsePaths(entry));
                logs.add(log);
            }
            return logs;
        } catch (Exception e) {
            throw new BusinessException("SVN XML 로그를 해석할 수 없습니다.", "SVN_LOG_PARSE_ERROR");
        }
    }

    private List<VcsChangeFileEntity> parsePaths(Element entry) {
        List<VcsChangeFileEntity> files = new ArrayList<>();
        NodeList paths = entry.getElementsByTagName("path");
        for (int i = 0; i < paths.getLength(); i++) {
            Node node = paths.item(i);
            if (node instanceof Element path) {
                VcsChangeFileEntity file = new VcsChangeFileEntity();
                file.setChangeType(path.getAttribute("action"));
                file.setFilePath(path.getTextContent().trim());
                files.add(file);
            }
        }
        return files;
    }

    private String text(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            return "";
        }
        return nodes.item(0).getTextContent().trim();
    }
}
