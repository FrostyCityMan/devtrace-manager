package com.devtrace.manager.common.pagination;

/**
 * 목록 조회용 기본 페이지 요청 값입니다.
 *
 * <p>페이지 번호와 크기는 1 이상으로 보정하며, MyBatis LIMIT/OFFSET 계산에 필요한
 * 오프셋 값을 제공합니다.</p>
 */
public class PageRequest {

    private int page = 1;
    private int size = 20;

    /**
     * 현재 페이지 번호를 반환합니다.
     *
     * @return 1부터 시작하는 페이지 번호
     */
    public int getPage() {
        return page;
    }

    /**
     * 페이지 번호를 설정합니다.
     *
     * @param page 1부터 시작하는 페이지 번호
     */
    public void setPage(int page) {
        this.page = Math.max(page, 1);
    }

    /**
     * 페이지 크기를 반환합니다.
     *
     * @return 페이지 크기
     */
    public int getSize() {
        return size;
    }

    /**
     * 페이지 크기를 설정합니다.
     *
     * @param size 페이지 크기
     */
    public void setSize(int size) {
        this.size = Math.max(size, 1);
    }

    /**
     * 데이터베이스 조회용 시작 오프셋을 계산합니다.
     *
     * @return LIMIT/OFFSET 조회에 사용할 오프셋
     */
    public int getOffset() {
        return (page - 1) * size;
    }
}
