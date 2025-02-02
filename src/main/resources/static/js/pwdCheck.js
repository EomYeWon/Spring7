/*
개인정보 수정시 필요한 검증 작업
*/


let pwdCheck = false;	//비번,비번확인인

//비번 
$(function () {
	$('submitBtn').on('click', validation);

});




// 2) 개인정보수정정을 위한 나머지 검증작업
function validation() {


	// 비밀번호 길이 체크
	let userPwd = $('#userPwd').val();

	if (userPwd.trim().length < 3 || userPwd.trim().length > 5) {
		$('#confirmPwd').css('color', 'red');
		$('#confirmPwd').html('비밀번호는 3~5자 이내');

		return false;
	}
	return true;

}
