/*
로그인인시 필요한 검증 작업
*/

let idCheck = false;
let pwdCheck = false;	//비번,비번확인인

//비번 
$(function () {
	$('submitBtn').on('click', login);

});
$('submitBtn').on('click', join);



// 2) 로그인인을 위한 나머지 검증작업
function join() {
	let userId = $('#userId').val();

	//1) 아이디 검증(길이 + 중복확인)
	function confirmId() {
		let userId = $('#userId').val();

		//길이 체크
		if (userId.trim().length < 3 || userId.trim().length > 5) {
			$('#confirmId').css('color', 'red');
			$('#confirmId').html('아이디는 3~5자이내');

			return false;

		}

		// 비밀번호 길이 체크
		let userPwd = $('#userPwd').val();

		if (userPwd.trim().length < 3 || userPwd.trim().length > 5) {
			$('#confirmPwd').css('color', 'red');
			$('#confirmPwd').html('비밀번호는 3~5자 이내');

			return false;
		}
		return true;



	}



}
