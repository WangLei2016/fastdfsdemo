package com.ssmm.util;

import org.apache.log4j.Logger;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
/**
 * @��Ŀ����:lidong-dubbo
 * @����:FastDFSUtil
 * @�������: FastDFS �ϴ��ļ����ļ�������
 * @����:lidong
 * @����ʱ��:2017/2/6 ����5:23
 * @��˾:chni
 * @QQ:1561281670
 * @����:lidong1665@163.com
 */
public class FastDFSUtil {

	private static Logger logger = Logger.getLogger(FastDFSUtil.class);  

	

    


    /**
     *�ϴ������������ļ�-ͨ��Linux�ͻ���,���ÿͻ��������ϴ�
     * @param filePath �ļ�����·��
     * @return Map<String,Object> code-���ش���, group-�ļ���, msg-�ļ�·��/������Ϣ
     */
    public static Map<String, Object> uploadLocalFile(String filePath) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        /**
         * 1.�ϴ��ļ�������
         */
        String command = "fdfs_upload_file /etc/fdfs/client.conf  " + filePath;
        /**
         * 2.�����ļ��ķ�����Ϣ
         */
        String fileId = "";
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            /**
             * 3.ͨ������api, ִ��linux�����ϴ��ļ�
             */
            Process process = Runtime.getRuntime().exec(command);
            /**
             * 4.��ȡ�ϴ��󷵻ص���Ϣ
             */
             inputStreamReader = new InputStreamReader(process.getInputStream());
             bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            if ((line = bufferedReader.readLine()) != null) {
                fileId = line;
            }
            /**
             * 5.���fileId����M00��˵���ļ��Ѿ��ϴ��ɹ��������ļ��ϴ�ʧ��
             */
            if (fileId.contains("M00")) {
                retMap.put("code", "0000");
                retMap.put("group", fileId.substring(0, 6));
                retMap.put("msg", fileId.substring(7, fileId.length()));
            } else {
                retMap.put("code", "0001");  //�ϴ�����
                retMap.put("msg", fileId);   //������Ϣ
            }

        } catch (Exception e) {
            logger.error("IOException:" + e.getMessage());
            retMap.put("code", "0002");
            retMap.put("msg", e.getMessage());
        }finally {
            if (inputStreamReader!=null){
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return retMap;
    }


    /**
     * Description: ֱ��ͨ��fdfs java�ͻ����ϴ���������-��ȡ�����ļ��ϴ�
     *
     * @param filePath �����ļ�����·��
     * @return Map<String,Object> code-���ش���, group-�ļ���, msg-�ļ�·��/������Ϣ
     */
    public static Map<String, Object> upload(String filePath) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        File file = new File(filePath);
        TrackerServer trackerServer = null;
        StorageServer storageServer = null;
        if (file.isFile()) {
            try {
                String tempFileName = file.getName();
           
                byte[] fileBuff = FileUtil.getBytesByFile(filePath);
                String fileId = "";
                //��ȡ��׺
                String fileExtName = tempFileName.substring(tempFileName.lastIndexOf(".") + 1);
                ConfigAndConnectionServer configAndConnectionServer = new ConfigAndConnectionServer().invoke(1);
                StorageClient1 storageClient1 = configAndConnectionServer.getStorageClient1();
                storageServer = configAndConnectionServer.getStorageServer();
                trackerServer = configAndConnectionServer.getTrackerServer();

                /**
              * 4.�����ļ���������ԡ����ÿͻ��˵�upload_file1�ķ����ϴ��ļ�
              */
                NameValuePair[] metaList = new NameValuePair[3];
                //ԭʼ�ļ�����
                metaList[0] = new NameValuePair("fileName", tempFileName);
                //�ļ���׺
                metaList[1] = new NameValuePair("fileExtName", fileExtName);
                //�ļ���С
                metaList[2] = new NameValuePair("fileLength", String.valueOf(file.length()));
                //��ʼ�ϴ��ļ�
                fileId = storageClient1.upload_file1(fileBuff, fileExtName, metaList);
                retMap = handleResult(retMap, fileId);
            } catch (Exception e) {
                e.printStackTrace();
                retMap.put("code", "0002");
                retMap.put("msg", e.getMessage());
            }finally {
                /**
                 * 5.�رո��ٷ�����������
                 */
                colse(storageServer, trackerServer);
            }
        } else {
            retMap.put("code", "0001");
            retMap.put("msg", "error:�����ļ�������!");
        }
        return retMap;
    }


    /**
     * Description:Զ��ѡ���ϴ��ļ�-ͨ��MultipartFile
     *
     * @param file �ļ���
     * @return Map<String,Object> code-���ش���, group-�ļ���, msg-�ļ�·��/������Ϣ
     */
    public static Map<String, Object> upload(MultipartFile file) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        TrackerServer trackerServer = null;
        StorageServer storageServer = null;
        try {
            if (file.isEmpty()) {
                retMap.put("code", "0001");
                retMap.put("msg", "error:�ļ�Ϊ��!");
            } else {
                ConfigAndConnectionServer configAndConnectionServer = new ConfigAndConnectionServer().invoke(1);
                StorageClient1 storageClient1 = configAndConnectionServer.getStorageClient1();
                storageServer = configAndConnectionServer.getStorageServer();
                trackerServer = configAndConnectionServer.getTrackerServer();
                String tempFileName = file.getOriginalFilename();
                //����Ԫ��Ϣ
                NameValuePair[] metaList = new NameValuePair[3];
                //ԭʼ�ļ�����
                metaList[0] = new NameValuePair("fileName", tempFileName);
                //�ļ���׺
                byte[] fileBuff = file.getBytes();
                String fileId = "";
                //��ȡ��׺
                String fileExtName = tempFileName.substring(tempFileName.lastIndexOf(".") + 1);

                metaList[1] = new NameValuePair("fileExtName", fileExtName);
                //�ļ���С
                metaList[2] = new NameValuePair("fileLength", String.valueOf(file.getSize()));
                /**
                 * 4.���ÿͻ����ص�upload_file1�ķ�����ʼ�ϴ��ļ�
                 */
                fileId = storageClient1.upload_file1(fileBuff, fileExtName, metaList);
                retMap = handleResult(retMap, fileId);
            }
        } catch (Exception e) {
            retMap.put("code", "0002");
            retMap.put("msg", "error:�ļ��ϴ�ʧ��!");
        }finally {
            /**
             * 5.�رո��ٷ�����������
             */
            colse(storageServer, trackerServer);
        }
        return retMap;
    }


    /**
     * �����ļ�
     *
     * @param response
     * @param filepath ���ݿ����ļ�·��
     * @param downname ���غ������
     *                 filepath M00/��ͷ���ļ�·��
     *                 group �ļ����ڵ��� �磺group0
     * @throws IOException
     */
    public static void download(HttpServletResponse response, String group, String filepath, String downname) {
        StorageServer storageServer = null;
        TrackerServer trackerServer = null;
        try {
            ConfigAndConnectionServer configAndConnectionServer = new ConfigAndConnectionServer().invoke(0);
            StorageClient storageClient = configAndConnectionServer.getStorageClient();
            storageServer = configAndConnectionServer.getStorageServer();
            trackerServer = configAndConnectionServer.getTrackerServer();

            /**
             *4.���ÿͻ��˵�����download_file�ķ���
             */
            byte[] b = storageClient.download_file(group, filepath);
            if (b == null) {
                logger.error("Error1 : file not Found!");
                response.getWriter().write("Error1 : file not Found!");
            } else {
                logger.info("�����ļ�..");
                downname = new String(downname.getBytes("utf-8"), "ISO8859-1");
                response.setHeader("Content-Disposition", "attachment;fileName=" + downname);
                OutputStream out = response.getOutputStream();
                out.write(b);
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.getWriter().write("Error1 : file not Found!");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }finally {
            /**
             * 5.�رո��ٷ�����������
             */
            colse(storageServer, trackerServer);
        }
    }

    /**
     * ɾ���ļ�
     *
     * @param group �ļ�����,  filepath ��M00/ ��ͷ���ļ�·��
     * @return Map<String,Object> code-���ش���,  msg-������Ϣ
     */
    public static Map<String, Object> delete(String group, String filepath) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        StorageServer storageServer = null;
        TrackerServer trackerServer = null;
        try {
            ConfigAndConnectionServer configAndConnectionServer = new ConfigAndConnectionServer().invoke(0);
            StorageClient storageClient = configAndConnectionServer.getStorageClient();
            storageServer = configAndConnectionServer.getStorageServer();
            trackerServer = configAndConnectionServer.getTrackerServer();
            /**
             * 4.���ÿͻ��˵�delete_file����ɾ���ļ�
             */
            int i = storageClient.delete_file(group, filepath);
            if (i == 0) {
                retMap.put("code", "0000");
                retMap.put("msg", "ɾ���ɹ���");
            } else {
                retMap.put("code", "0001");
                retMap.put("msg", "�ļ�������!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            retMap.put("code", "0002");
            retMap.put("msg", "ɾ��ʧ�ܣ�");
        } finally {
            /**
             * 5.�رո��ٷ�����������
             */
            colse(storageServer, trackerServer);
        }

        return retMap;

    }

    /**
     * �رշ�����
     *
     * @param storageServer
     * @param trackerServer
     */
    private static void colse(StorageServer storageServer, TrackerServer trackerServer) {
        if (storageServer != null && trackerServer != null) {
            try {
                storageServer.close();
                trackerServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * �����ϴ����ļ�������֮�󣬷������Ľ��
     *
     * @param retMap
     * @param fileId
     * @return
     */
    private static Map<String, Object> handleResult(Map<String, Object> retMap, String fileId) {
        if (!fileId.equals("") && fileId != null) {
            retMap.put("code", "0000");
            retMap.put("group", fileId.substring(0, 6));
            retMap.put("msg", fileId.substring(7, fileId.length()));
        } else {
            retMap.put("code", "0003");
            retMap.put("msg", "error:�ϴ�ʧ��!");
        }

        return retMap;
    }

    /**
     * @��Ŀ����:lidong-dubbo
     * @����:FastDFSUtil
     * @�������: ConfigAndConnectionServer
     * @����:lidong
     * @����ʱ��:2017/2/7 ����8:47
     * @��˾:chni
     * @QQ:1561281670
     * @����:lidong1665@163.com
     */
    private static class ConfigAndConnectionServer {
        private TrackerServer trackerServer;
        private StorageServer storageServer;
        private StorageClient storageClient;
        private StorageClient1 storageClient1;


        public TrackerServer getTrackerServer() {
            return trackerServer;
        }

        public StorageServer getStorageServer() {
            return storageServer;
        }

        public StorageClient getStorageClient() {
            return storageClient;
        }

        public StorageClient1 getStorageClient1() {
            return storageClient1;
        }

        public ConfigAndConnectionServer invoke(int flag) throws IOException, MyException {
            /**
             * 1.��ȡfastDFS�ͻ��������ļ�
             */
            ClassPathResource cpr = new ClassPathResource("fdfs_client.conf");
            /**
             * 2.�����ļ��ĳ�ʼ����Ϣ
             */
            ClientGlobal.init(cpr.getClassLoader().getResource("fdfs_client.conf").getPath());
            TrackerClient tracker = new TrackerClient();
            /**
             * 3.��������
             */
            trackerServer = tracker.getConnection();
            storageServer = null;
            /**
             * ���flag=0ʱ�򣬹���StorageClient���������StorageClient1
             */
            if (flag == 0) {
                storageClient = new StorageClient(trackerServer, storageServer);
            } else {
                storageClient1 = new StorageClient1(trackerServer, storageServer);
            }
            return this;
        }
    }
}