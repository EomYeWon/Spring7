/*
페이징 호출 코드
*/

function pageFromSubmit(page) {
    $('#requestPage').val(page);
    $('#searchForm').submit();



    // location.href = `/board/boardList?page=${page}` 다른방법으로 해볼거임. 검색후 페이징을하면 검색이 풀림.
}