import java.io.File;
import java.io.IOException;

    //本Main文件夹主要用于实现命令
    //为了保证命令页美观，本页不再增加过多代码
    //因此将加入相关注释

public class git {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        //再此步骤检验是否发生异常，如发生异常则返回
        //如果不加这部检验系统报错

        if(args == null || args.length <= 0) return;
            //判断是否执行int命令，对应gitInit界面

                if (args[0].equals("init")) {
                    System.out.println("开始执行git命令");
                    gitInit.init();
                    //输出根目录方便查询代码运行结果
                    System.out.println(System.getProperty("user.dir"));

                } else if (args[0].equals("add")) {
                    //在本次作业中只提交addOnce运行命令及结果，后续还会更新add.命令等
                    System.out.println("开始执行add命令");
                    if(args.length == 2){
                        if (args[1].equals(".")) {
                            gitAdd.addALl();
                        } else gitAdd.addOnce(args[1]);
                    }else System.out.println("add命令格式输入有误");


                } else if (args[0].equals("commit")) {
                    System.out.println("开始执行commit命令");
                    //判断一下是否命令输入正确
                    if(args[1].equals("-m")){
                        //arg[3]为message内容
                        gitCommit.commitOperation(args[2]);
                    }else System.out.println("您的命令输入有误！请查看命令格式是否为 git commit -m message");
                } else if (args[0].equals("rm")) {
                    System.out.println("开始执行rm操作");
                    if (args[1].equals("--cached")) {
                        gitRm.rmcached(args[2]);
                    } else if (args[1].equals("-branch")) {
                        gitBranch.deleteBranch(args[2]);
                    } else gitRm.rmFILE(args[1]);

                } else if (args[0].equals("log")) {
                    System.out.println("开始执行log操作");
                    gitLog.commitLog();
                } else if (args[0].equals("reset")) {
                    System.out.println("开始执行reset操作");
                    if (args[1].equals("--soft")) {
                        gitReset.soft(args[2]);
                    } else if (args[1].equals("--hard")) {
                        gitReset.hard(args[2]);
                    } else if (args.length == 2) {//如果只有2位，说明是直接输入了 reset commitid
                        //执行reset的默认操作也就是mix
                        gitReset.mix(args[1]);
                    } else if (args[1].equals("--mix")) {
                        gitReset.mix(args[2]);
                    }
                } else if (args[0].equals("push")) {
                    System.out.println("开始执行push操作");
                    gitPullPush.Pull_Push(gitInit.gitPath, gitInit.depoPath);
                } else if (args[0].equals("pull")) {
                    System.out.println("开始执行pull操作");
                    gitPullPush.Pull_Push(gitInit.depoPath, gitInit.gitPath);
                } else if (args[0].equals("reflog")) {
                    System.out.println("开始执行reflog操作");
                    gitReflog.Reflog();
                }else if (args[0].equals("status")){
                    System.out.println("开始执行status操作");
                    gitStatus.checkStatus();
                }else if(args[0].equals("diff")){
                    gitDiff.diff();
                }else if(args[0].equals("branch")){
                    //branch另一个操作在rm里
                    gitBranch.newBranch(args[1]);
                }else if(args[0].equals("find")){
                    gitFind.findMessage(args[1]);
                }else if(args[0].equals("checkout")){
                    gitCheckout.checkout(args[1]);
                }


            //如果没有该命令，就报错
            else {
                System.out.println("警告！您输入的命令有误或当前不支持该命令！请重新输入！");
            }

    }
}


