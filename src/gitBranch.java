import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class gitBranch {
    public static void newBranch(String branchname){
        //通过branchname确定这个分支的head的文件地址
        String branchPath = gitInit.headsDir + File.separator + branchname + ".txt";
        File file = new File(branchPath);
        if(file.exists()) System.out.println(branchname + "分支已经存在");
        else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //将最近的commitId传入该分支的head文件中
            String commitID = null;
            try {
                commitID = gitAdd.fileContent(gitInit.hdPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                bw.write(commitID);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                bw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(branchname + "分支创建成功");
        }

    }
    public static void deleteBranch(String branchname){
        //该操作用于实现rm -branch <branchname>
        String branchPath = gitInit.headsDir + File.separator + branchname + ".txt";
        File file2 = new File(branchPath);
        if(!file2.exists()) System.out.println("错误:"+branchname+"分支不存在");
        else{
            if(file2.delete()){
                //在这一步再加一个判断，防止操作失误
                System.out.println(branchname + "分支已经删除成功");
            }else{
                System.out.println(branchname + "分支删除失败");
            }
        }
    }
}
