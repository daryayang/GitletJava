import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class gitPullPush {
    public static void Pull_Push(String orPath, String taPath) throws IOException, ClassNotFoundException {
        String obdir = orPath + File.separator + "objects";
        File dir = new File(obdir);
        File[] files = dir.listFiles();
        //如果目标传送仓库中没有object文件夹或者目标传送地址有问题，将报错
        for(File f:files){
            if(f.isFile()){
                //Server端打开
                ServerSocket serverSocket;
                serverSocket = new ServerSocket(1233);

                System.out.println("服务器端：端口号为1233 " + "ip地址为" + InetAddress.getLocalHost());

                //clent端打开连接
                Socket socket1 = new Socket(InetAddress.getLocalHost(),1233);
                System.out.println("客户端：端口号为1233 " + "ip地址为" + InetAddress.getLocalHost());

                Socket socket ;
                socket = serverSocket.accept();
                System.out.println("连接成功！");

                String filename = f.getName();
                String filepath = obdir + File.separator + filename;
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filepath));
                ObjectOutputStream ops = new ObjectOutputStream(socket.getOutputStream());
                ops.writeObject(ois.readObject());
                socket.shutdownOutput();

                //关闭资源
                ops.close();
                ois.close();
                socket.close();


                //在client端接收
                ObjectInputStream ois1 = new ObjectInputStream(socket1.getInputStream());
                Object o = ois1.readObject();

                ois1.close();
                String filename2;
                if(o instanceof String){
                    //如果这个类型是string，说明是blob文件
                    //那么文件名字就是这个string生成的
                    String result = (String) o;
                    filename2 = taPath + File.separator + "objects" + File.separator + gitAdd.getHV(result) + ".txt";
                    File file1 = new File(filename2);
                    if(!file1.exists()){
                        file1.createNewFile();
                    }
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file1));
                    oos.writeObject(result);
                    oos.close();
                }else if(o instanceof HashMap){
                    //如果是hashmap类型，说明是tree文件或者是commit文件
                    //如果是commit文件，它的key中有treeid和message
                    //并且它的名字是由这两个算出来的
                    HashMap hashMap = (HashMap) o;
                    if(hashMap.containsKey("message") && hashMap.containsKey("treeid")){
                        String content = (String) hashMap.get("treeid") + (String) hashMap.get("message");
                        filename2 = taPath + File.separator + "objects" + File.separator + gitAdd.getHV(content) + ".txt";
                        File file2 = new File(filename2);
                        if(!file2.exists()){
                            file2.createNewFile();
                        }
                        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file2));
                        oos.writeObject(hashMap);
                        oos.close();
                    }else{
                        //如果是tree文件，文件名字是由index文件通过gitadd里面的getcontent方法算出来的
                        // 它里面的hashmap就是index的hashmap，所以自己算自己就行
                        //这里用了个toString为了和fileContentOb方法保持一致
                        String result = o.toString();
                        filename2 = taPath + File.separator + "objects" + File.separator + gitAdd.getHV(result) + ".txt";
                        File file3 = new File(filename2);
                        if(!file3.exists()){
                            file3.createNewFile();
                        }
                        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file3));
                        oos.writeObject(hashMap);
                        oos.close();

                    }


                } else  {
                    System.out.println("文件传输出现错误，请检查原仓库");
                }

                //4.关闭相关资源
                socket1.close();
                serverSocket.close();

                //执行resetHard操作

            }

        }



        //之所以选择在这里选择用resetHard命令，一方面是因为老师要求
        //另一方面是节省socket资源
        //从源仓库的head文件中读取最近一次commitid
        String commitId = gitAdd.fileContent(orPath + File.separator + "HEAD.txt");
        //然后将这个commitid作为参数（输入）到reset命令并执行reset操作
        gitReset.hardPP(commitId,taPath);



        //实现reflog命令之后在这个基础上补充HEAD_B文件的传输
        String orheadB = orPath + File.separator + "HEAD_B.txt";
        String taheadB = taPath + File.separator + "HEAD_B.txt";

        ServerSocket serverSocket;
        serverSocket = new ServerSocket(1233);

        System.out.println("服务器端：端口号为1233 " + "ip地址为" + InetAddress.getLocalHost());

        //clent端打开连接
        Socket socket1 = new Socket(InetAddress.getLocalHost(),1233);
        System.out.println("客户端：端口号为1233 " + "ip地址为" + InetAddress.getLocalHost());

        Socket socket ;
        socket = serverSocket.accept();
        System.out.println("连接成功！");

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(orheadB));
        ObjectOutputStream ops = new ObjectOutputStream(socket.getOutputStream());
        ops.writeObject(ois.readObject());
        socket.shutdownOutput();

        //关闭资源
        ops.close();
        ois.close();
        socket.close();

        //在client端接收
        ObjectInputStream ois1 = new ObjectInputStream(socket1.getInputStream());
        Object o = ois1.readObject();
        ois1.close();
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(taheadB));
        oos.writeObject(o);
        oos.close();

        socket1.close();
        serverSocket.close();



        System.out.println("接收端已经接收成功");

                    }}
