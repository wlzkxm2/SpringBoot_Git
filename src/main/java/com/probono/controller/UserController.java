package com.probono.controller;

import com.probono.entity.Files;
import com.probono.entity.User;
import com.probono.repo.FileRepo;
import com.probono.repo.UserRepo;
import lombok.extern.java.Log;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@Controller
public class UserController {

	@Autowired(required = true)
	private UserRepo userRepo;

	@Autowired(required = true)
	private FileRepo fileRepo;

//	private FileService fileService;
	
	@GetMapping("/")		// 앱 시작할때
	public String login(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		return "upload";		// 반환해주는 html이름
	}

// 업로드한 파일 설정
	@RequestMapping(value = {"/upload"})
	public ResponseEntity<Map<String, String>> upload(List<MultipartFile> files, HttpServletRequest request) {

		files.forEach(file -> {
			System.out.println(file.getContentType());	// 업로드 받은 파일 타입 디버깅
			System.out.println(file.getOriginalFilename());	// 업로드 받은 받은 파일 이름

			String sourceFileName = file.getOriginalFilename();		// 업로드 받은 원본 파일 이름 저장
			String sourceFileExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase();	// 업로드 받은 원본파일 소문자로 저장
			int inputid = sourceFileName.lastIndexOf("_");
			String findid = sourceFileName.substring(0, inputid);

			FilenameUtils.removeExtension(sourceFileName);

			File destinationFile;
			String destinationFileName;
			String fileUrl = "D:\\Shingu\\shingu\\SpringBoot_Git\\probono_login\\src\\main\\src\\main\\resources\\static\\SpringDB\\";	// 다운경로

			do{
				destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFileExtension;		// 파일 이름 랜덤 암호화
				destinationFile = new File(fileUrl + destinationFileName);
			}while (destinationFile.exists());

			destinationFile.getParentFile().mkdirs();
			try {
				file.transferTo(destinationFile);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			try{
				System.out.println("final android id : " + findid);
//
				Optional<Files> findFile = fileRepo.findById(findid);
//				System.out.println("찾은 파일 이름 : " + findFile.get().getFileOriname());
				if(findFile.isEmpty() == false){
					fileRepo.deleteById(findid);		// 만약 파일있다면 삭제
//					String deleteFilePath = "D:\\Shingu\\shingu\\SpringBootDocument\\probono_login\\src\\main\\src\\main\\resources\\static\\SpringDB\\" + findFile.get().getFilename();
//					File deleteFile = new File(deleteFilePath);
					System.out.println("저장된 파일 이름 : " + findFile.get().getFilename());
					System.out.println("저장된 파일 경로 : " + findFile.get().getFileUrl());
					String Filename = findFile.get().getFilename();		// 삭제할 파일의 이름
					String FilePath = findFile.get().getFileUrl();		// 삭제할 파일의 경로

					String deleteFilePath = FilePath + Filename;
					System.out.println("완전체 : " + deleteFilePath);
					File deleteFile = new File(deleteFilePath);		// 삭제할 파일을 타겟
					deleteFile.delete();			// 파일을 삭제
					System.out.println("파일을 삭제 하엿습니다");

				}

				Files saveFile = new Files();
				saveFile.setFilename(destinationFileName);
				saveFile.setFileOriname(sourceFileName);
				saveFile.setFileUrl(fileUrl);
				saveFile.setUserID(findid);
				Files addFile = fileRepo.save(saveFile);


			} catch (Exception e){

			}

		});

		HashMap<String, String> resultMap = new HashMap<>();
		resultMap.put("result", "success");


		return ResponseEntity.ok(resultMap);
	}

	@RestController
	@RequestMapping("/api")
	public class Sample{

		String getFileName;
// 업로드 되어있는 파일 다운
		@GetMapping("/download/{fileName:.+}")
		public void download(HttpServletResponse response, HttpServletRequest request, @PathVariable String fileName) throws IOException{
			String path = "D:\\Shingu\\shingu\\SpringBoot_Git\\probono_login\\src\\main\\src\\main\\resources\\static\\SpringDB\\" + fileName;
			byte[] fileByte = FileUtils.readFileToByteArray(new File(path));

			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition",
					"attachment; fileName=\""
							+ URLEncoder.encode(fileName, "UTF-8")
							+"\";");
			response.setHeader("Content-Transfer-Encoding", "binary");

			response.getOutputStream().write(fileByte);
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}

		@RequestMapping(value = "/datacheak")
		public String dataPage(HttpServletRequest request){
			System.out.println("데이터 체크를 위해 접속");

			try {
				String androidId = request.getParameter("id");
				System.out.println("데이터 확인을 위해 입력받은 아이디 : " + androidId);

				// 해당 유저의 파일 이름을 받아옴
				Optional<Files> findFile = fileRepo.findById(androidId);
				String FileName = findFile.get().getFilename();
				getFileName = FileName;

				return FileName;

			}catch (Exception e){
				System.out.println(e);
				return "null";
			}
		}
	}

	@PostMapping("/userLogin")		// /userLogin 이라는 주소로보냄
	public String loginUser(@ModelAttribute("user") User user){		// 입력된 데이터 전송받음
		System.out.println(user.getUserID());
		System.out.println(user.getPassword());

		String userId = user.getUserID();
		System.out.println("test : " + userId);

		Optional<User> userdata = userRepo.findById(userId);

		if (user.getPassword().equals(userdata.get().getPassword())){
			return "home";
		}else{
			return "error";
		}
	}


	@RestController
	@RequestMapping("/android")
	public class android{

		//, method = {RequestMethod.POST}
		@RequestMapping(value="/login")
		public String androidPage(@ModelAttribute("user") User user, HttpServletRequest request, Model model){
			System.out.println("서버에서 안드로이드 접속 ");
			try{
				String androidID = request.getParameter("id");
				String androidPW = request.getParameter("pw");

				System.out.println("입력받은 androidId : " + androidID);
				System.out.println("입력받은 androidPW : " + androidPW);

				Optional<User> userdata = userRepo.findById(androidID);

				if (userdata.isEmpty() == false){		// 입력받은 데이터가 존재할 경우
					System.out.println(userdata);
					if (userdata.get().getPassword().equals(androidPW)){
						System.out.println("PassWord Success");
						return userdata.get().toString();
					}else{
						System.out.println("ID Success / Password Fail");
						System.out.println("pw : " + userdata.get().getPassword());
						return null;		// 패스워드가 맞지 경우
					}
				}else{									// 존재하지 않을 경우
					System.out.println("null");

					return null;
				}
			} catch (Exception e){
				e.printStackTrace();
				return "null";
			}
		}

		@RequestMapping(value = "/andregister")
		public String rigisterPage(@ModelAttribute("user") User user, HttpServletRequest request, Model mode){
			System.out.println("회원가입 페이지 접속");
			try{
				int FO = 0, LO = 0;
				String findresults = "";

//				String inputdata = request.getParameter("id");

				String registerID = request.getParameter("id");
				String registerPW = request.getParameter("pw");
				String registeremail = request.getParameter("email");
				String registerusername = request.getParameter("name");
				String registerphonenumber = request.getParameter("phone");
				int registerage = Integer.parseInt(request.getParameter("age"));
				String registeraddress = request.getParameter("address");
				String registerdetailaddress = request.getParameter("detailaddress");
				String registerzipcode = request.getParameter("zipcode");

				System.out.printf("받은 데이터  : %s / %s / %s / %s / %s / %s / %s / %s / %s" ,
//						inputdata,
						registerID,
						registerPW,
						registeremail,
						registerusername,
						registerphonenumber,
						registerage,
						registeraddress,
						registerdetailaddress,
						registerzipcode
				);

				User registeruser = new User();
				registeruser.setUserID(registerID);
				registeruser.setPassword(registerPW);
				registeruser.setEmail(registeremail);
				registeruser.setAge(registerage);
				registeruser.setName(registerusername);
				registeruser.setPN(registerphonenumber);
				registeruser.setAddressCity(registeraddress);
				registeruser.setAddressState(registerdetailaddress);
				registeruser.setZIPCode(registerzipcode);

				User addUser = userRepo.save(registeruser);

				System.out.println("회원 가입 완료");

				Optional<User> byId = userRepo.findById(registerID);
				System.out.println("유저정보 : " + byId);

				return "login";
			} catch (Exception e){
				return "error";
			}
		}

		@RequestMapping(value = "IDCheck")
		public String IDcheckPage(@ModelAttribute("user") User user, HttpServletRequest request){
			try{
				String getID = request.getParameter("id");
				System.out.println("받은 ID : " + getID);

				Optional<User> userdata = userRepo.findById(getID);

				System.out.println(userdata);
				if (userdata.isEmpty()) {
					System.out.println("비어있다");
					return "true";
				}else return "false";


			}catch (Exception e){
				return "없는 ID";
			}
		}
	}

	/*
	@PostMapping("/upload")
	public String uploadFile(@RequestPart MultipartFile files) throws IOException{
		Files file = new Files();

		String sourceFileName = files.getOriginalFilename();			// 전송받은 파일의 이름을 가져옴
		String sourceFileExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase();	// 마지막 . 뒤를 반환해줌
		FilenameUtils.removeExtension(sourceFileName);		// . 앞을 반환해줌

		File destinationFile;
		String destinationFileName;
		String fileUrl = "D:\\shingu\\SpringBootDocument\\probono_login\\src\\main\\resources\\static\\SpringDB\\";

		do{
			destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFileExtension;
			destinationFile = new File(fileUrl + destinationFileName);
		}while (destinationFile.exists());

		destinationFile.getParentFile().mkdir();
		files.transferTo(destinationFile);

		Files saveFiles = new Files();
		saveFiles.setFilename(destinationFileName);
		saveFiles.setFileOriname(sourceFileName);
		saveFiles.setFileUrl(fileUrl);
//		fileService.saveFile(saveFiles);
		Files addFile = fileRepo.save(saveFiles);

		return "redirect:/upload";
	}
	 */


}
