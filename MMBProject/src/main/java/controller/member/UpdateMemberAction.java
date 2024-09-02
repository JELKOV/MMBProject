package controller.member;

import controller.common.Action;
import controller.common.ActionForward;
import controller.common.ProfilePicUpload;
import model.dao.MemberDAO;
import model.dto.MemberDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class UpdateMemberAction implements Action{
	// 이메일 변경

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("	log : UpdateMemberAction.java		시작");

		// 프로필사진을 담을 변수 선언
		String profilePic = null;
		// 이전 프로필사진을 담을 변수 선언
		// 업데이트 성공시 파일 삭제를 위해 파일명을 복사해놓음
		String preProfilePic = null;

		// session(memberPK)에서 회원번호(PK)값 가져오기
		HttpSession session = request.getSession();
		int memberPK = (int) session.getAttribute("memberPK");
		System.out.println("	log : UpdateMemberAction.java		session(memberPK) : "+ memberPK);

		// V에서 이메일, 닉네임, 비밀번호, 전화번호 받아오기
		String email = request.getParameter("email");
		System.out.println("	log : UpdateMemberAction.java		email : " + email);
		String password = request.getParameter("password");
		System.out.println("	log : UpdatePasswordAction.java		password : " + password);
		String nickName = request.getParameter("nickName");
		System.out.println("	log : UpdateNickNameAction.java		nickName : " + nickName);
		String phoneNum = request.getParameter("phoneNum");
		System.out.println("	log : UpdatePhoneAction.java		phoneNum : " + phoneNum);

		// 기존 프로필 사진 받아오기
		preProfilePic = ProfilePicUpload.loginProfilePic(request, response);
		System.out.println("	log : UpdateProfilePicAction.java		이전 프로필사진 : "+ profilePic);
		// 입력된 파일을 확인하고 저장하는 메서드
		// 저장이 완료된 파일명을 반환
		profilePic = ProfilePicUpload.profilePicUpload(request, response);
		System.out.println("	log : UpdateProfilePicAction.java		profilePic : "+ profilePic);

		// MemberDTO memberDTO, DAO memberDAO 객체 new 생성
		MemberDTO memberDTO = new MemberDTO();
		MemberDAO memberDAO = new MemberDAO();

		// 만약 비밀번호가 null이 아니라면
		if(password != null) {
			// memberDTO에 condition : EMAIL_UPDATE 넣어주기
			memberDTO.setCondition("MEMBER_UPDATE"); // 나중 수정 예상
			// memberDTO에 비밀번호 값 넣어주기
			memberDTO.setMemberPassword(password); // 나중 수정 예상
		}
		// null이라면
		else {
			// memberDTO에 condition : MEMBER_NOT_PASSWORD_UPDATE 넣어주기
			memberDTO.setCondition("MEMBER_NOT_PASSWORD_UPDATE"); // 나중 수정 예상
		}

		// memberDTO에 회원번호(PK), 이메일, 닉네임, 전화번호 값 넣어주기
		// memberDTO에 이메일 값 넣어주기
		System.out.println("	log : UpdateMemberAction.java		condition : "+ memberDTO.getCondition());
		memberDTO.setMemberNum(memberPK);
		memberDTO.setMemberEmail(email);
		memberDTO.setMemberNickname(nickName);
		memberDTO.setMemberPhone(phoneNum);
		memberDTO.setMemberProfileWay(profilePic);
		System.out.println("	log : UpdateMemberAction.java		memberDTO에 set데이터 완료");

		// MemberDAO.update 요청
		// 결과값(boolean flag) 받아오기
		boolean flag = memberDAO.update(memberDTO);
		System.out.println("	log : UpdateMemberAction.java		memberDAO.update 요청");
		System.out.println("	log : UpdateMemberAction.java		update 결과 flag : "+ flag);

		// 만약 update에 성공했다면
		if(flag) {
			// 이전 프로필사진 지우기
			ProfilePicUpload.deletefile(preProfilePic);
		}
		// 만약 실패했다면
		else {
			// 입력한 프로필사진 지우기
			ProfilePicUpload.deletefile(profilePic);
		}
		
		// View에서 결과 출력용
		// request.setAttribute에 flag 저장
		request.setAttribute("result", flag);
		System.out.println("	log : UpdateMemberAction.java		V에게 update 결과 result를 보냄");

		// 페이지 이동 (결과에 따라 다른 페이지)
		// forward 객체 생성
		// 이동 방법 : 전송할 데이터가 있으므로 forward(false)
		ActionForward forward = new ActionForward();
		forward.setRedirect(false);

		// 업데이트 성공 실패에 상관없이 수정 페이지로 이동
		// 이동할 페이지 : logout.do
		forward.setPath("logout.do");

		System.out.println("	log : UpdateMemberAction.java		forwardPath : "+ forward.getPath());
		System.out.println("	log : UpdateMemberAction.java		종료");
		return forward;
	}
}