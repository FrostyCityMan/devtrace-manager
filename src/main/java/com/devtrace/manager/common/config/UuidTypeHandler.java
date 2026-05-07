package com.devtrace.manager.common.config;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * PostgreSQL UUID 값을 Java {@link UUID} 타입으로 매핑하는 MyBatis 타입 핸들러입니다.
 *
 * <p>PostgreSQL JDBC 드라이버가 UUID를 {@code OTHER} 타입으로 다루는 경우에도
 * DAO와 DTO에서는 일관되게 {@link UUID}를 사용할 수 있게 합니다.</p>
 */
@MappedTypes(UUID.class)
@MappedJdbcTypes(JdbcType.OTHER)
public class UuidTypeHandler extends BaseTypeHandler<UUID> {

    /**
     * UUID 파라미터를 JDBC {@code OTHER} 타입으로 설정합니다.
     *
     * @param ps PreparedStatement
     * @param i 파라미터 인덱스
     * @param parameter UUID 값
     * @param jdbcType JDBC 타입
     * @throws SQLException JDBC 설정 실패 시
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter, Types.OTHER);
    }

    /**
     * 컬럼명 기준 조회 결과를 UUID로 변환합니다.
     *
     * @param rs ResultSet
     * @param columnName 컬럼명
     * @return UUID 값
     * @throws SQLException JDBC 조회 실패 시
     */
    @Override
    public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toUuid(rs.getObject(columnName));
    }

    /**
     * 컬럼 인덱스 기준 조회 결과를 UUID로 변환합니다.
     *
     * @param rs ResultSet
     * @param columnIndex 컬럼 인덱스
     * @return UUID 값
     * @throws SQLException JDBC 조회 실패 시
     */
    @Override
    public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toUuid(rs.getObject(columnIndex));
    }

    /**
     * CallableStatement 조회 결과를 UUID로 변환합니다.
     *
     * @param cs CallableStatement
     * @param columnIndex 컬럼 인덱스
     * @return UUID 값
     * @throws SQLException JDBC 조회 실패 시
     */
    @Override
    public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toUuid(cs.getObject(columnIndex));
    }

    /**
     * JDBC 드라이버가 반환한 값을 UUID로 정규화합니다.
     *
     * @param value JDBC 원본 값
     * @return UUID 값
     */
    private UUID toUuid(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof UUID uuid) {
            return uuid;
        }
        return UUID.fromString(value.toString());
    }
}
