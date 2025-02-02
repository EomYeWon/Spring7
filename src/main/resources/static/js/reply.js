
$(function () {
    //첨부파일이 있을 경우 삭제
    $('.deleteFile').on('click', function () {
        let boardSeq = $(this).attr('data-seq');
        let searchItem = $(this).attr('data-searchItem');
        let searchWord = $(this).attr('data-searchWord');


        let answer = confirm("정말로 삭제하시겠습니까?");
        if (answer) {
            location.href = "/board/deleteFile?boardSeq=" + boardSeq + "&searchItem=" + searchItem + "&searchWord=" + searchWord;
        } else {
            alert("삭제작업을 취소합니다.")
        }
    });

    init(); //전체 댓글 읽어서 화면에 출력

    //댓글 입력
    $('#replyBtn').on('click', replyInsert);
    $('#replyUpdateProc').on('click', replyUpdateProc);
    $('#replyCancel').on('click', replyCancel);
});

//댓글 전체조회
function init() {
    let boardSeq = $('#boardSeq').val();

    $.ajax({
        url: '/reply/replyAll',
        method: 'GET',
        data: { "boardSeq": boardSeq },
        success: output
    })
}
//댓글 입력
function replyInsert() {
    let replyContent = $('#replyContent').val();
    let boardSeq = $('#boardSeq').val();

    if (replyContent.trim().length == 0) {
        alert("댓글을 입력하세요");
        return;
    }

    let sendData = { "boardSeq": boardSeq, "replyContent": replyContent };

    //ajax로 댓글 송신
    $.ajax({
        url: '/reply/replyInsert',
        method: 'POST',
        data: sendData,
        success: function (resp) {
            init();
            $('#replyContent').val('');
        }
    })
};

// 댓글 출력
function output(resp) {
    let loginId = $('#loginId').val();
    let tag = `
    <table>
        <tr>"
            <th >번호</th>
            <th >내용</th>
            <th >글쓴이</th>
            <th >날짜</th>
            <th ></th>
        </tr>
        
    
    `;

    $.each(resp, function (index, item) {       //resp = [{}, {}, {}]
        tag += `
        <tr>
            <td class='no'>${index + 1}</td>
            <td class='content'>${item['replyContent']}</td>
            <td class='writer'>${item['replyWriter']}</td>
            <td class='date'>${item['createDate']}</td>
            <td class='btns'>
                <input type="button" value="삭제" class="btn btn-danger deleteBtn"
                    data-seq="${item['replySeq']}" 
                    ${item['replyWriter'] == loginId ? '' : 'disabled'}>
                <input type="button" value="수정" class="btn btn-secondary updateBtn"
                    data-seq="${item['replySeq']}"
                     ${item['replyWriter'] == loginId ? '' : 'disabled'}>
            </td>
        </tr>
        `
    });
    tag += `</table >`;
    $('#reply_list').html(tag);

    $('.deleteBtn').on('click', replyDelete);
    $('.updateBtn').on('click', replyUpdate);
}

//댓글 삭제 함수
function replyDelete() {
    let replySeq = $(this).attr('data-seq');

    let answer = confirm('삭제하시겠습니까??');

    if (!answer) return;
    $.ajax({
        url: '/reply/replyDelete',
        method: 'GET',
        data: { "replySeq": replySeq },
        success: init
    });
}

//댓글 수정을 위한 조회  함수.
function replyUpdate() {
    let replySeq = $(this).attr('data-seq');
    $.ajax({
        url: '/reply/replyUpdate',
        method: 'GET',
        data: { "replySeq": replySeq },
        success: function (resp) {  //resp = {"" : ,"": }
            let Content = resp["replyContent"];

            $("#replyContent").val(Content);

            //버튼을 뒤집는다.
            $("#replyBtn").css("display", "none");

            $("#replyUpdateProc").css("display", "inline-block");
            $("#replyCancel").css("display", "inline-block");
        }
    });

    $('#replyUpdateProc').attr('data-seq', replySeq);
}
// 댓글 수정 처리 함수
function replyUpdateProc() {
    let replySeq = $(this).attr('data-seq');

    let replyContent = $('#replyContent').val();


    $.ajax({
        url: '/reply/replyUpdateProc',
        method: 'GET',
        data: {
            "replySeq": replySeq, "replyContent": replyContent
        },
        success: function (resp) {
            if (resp) {
                init();
                // $('#replyContent').val(replyContent);

                //버튼을 뒤집는다.
                $('#replyBtn').css('display', 'none');

                $('#replyUpdateProc').css('display', 'inline-block');
                $('#replyCancel').css('display', 'inline-block');
                $('#replyContent').val('');
            }

            replyCancel();
        },
    });

}

//수정처리 취소 함수
function replyCancel() {

    $('#replyBtn').css('display', 'inline-block');

    $('#replyUpdateProc').css('display', 'none');
    $('#replyCancel').css('display', 'none');
    $('#replyContent').val('')
}



