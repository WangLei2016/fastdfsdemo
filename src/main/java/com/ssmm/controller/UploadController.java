package com.ssmm.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.ssmm.util.FastDFSUtil;

@Controller
public class UploadController {
	
	private static Logger logger = Logger.getLogger(UploadController.class);  

	@RequestMapping("/upload")
	@ResponseBody
    public String addUser(@RequestParam("imgFile") CommonsMultipartFile[] files,
    		 HttpServletRequest req, HttpServletResponse res){
	/*	MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) req;
		
		          MultipartFile file  =  multipartRequest.getFile("imgFile");
	           byte[] photo = file.getBytes();*/
        for(int i = 0;i<files.length;i++){
            logger.info("fileName-->" + files[i].getOriginalFilename()+"     file-size--->"+files[i].getSize());
            Map<String, Object> retMap = FastDFSUtil.upload(files[i]);
            String code = (String) retMap.get("code");
            String group = (String) retMap.get("group");
            String msg = (String) retMap.get("msg");
            logger.info("group-->" + group+"msg--->"+msg);
            if ("0000".equals(code)){
                logger.info("文件上传成功");
                //TODO:将上传文件的路径保存到mysql数据库
            }else {
                logger.info("文件上传失败");
            }


        }
        return "/success";
    }

}
