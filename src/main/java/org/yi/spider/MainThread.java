package org.yi.spider;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yi.spider.enums.ParamEnum;
import org.yi.spider.helper.CliHelper;
import org.yi.spider.helper.CmdHelper;
import org.yi.spider.helper.FileHelper;
import org.yi.spider.processor.CmdProcessor;

/**
 * 
 * @ClassName: MainThread
 * @Description: 根据是否传入参数决定是否打开UI
 * @author QQ  
 *
 */
public class MainThread {
	
	private static final Logger logger = LoggerFactory.getLogger(MainThread.class);
	
	private String[] args;
	
	public MainThread(String[] args) {
		super();
		this.args = args;
	}

	public void run() {
		
		CommandLine cmd = null;
		try {
			if(args == null || args.length==0) {
				args =  new String[]{"-ca"};
				
			}
			cmd = CliHelper.parse(args);

			//help、version、m参数优先级  help > version > m
    		if (cmd.hasOption(ParamEnum.HELP.getName())) {
    			CmdHelper.showHelp();
    		} else if (cmd.hasOption(ParamEnum.VERSION.getName())) {
    			CmdHelper.showVersion();
    		} else if(cmd.hasOption(ParamEnum.MULTI.getName())) {
    			//读取运行时配置文件， 根据配置开启多线程执行, 如果开启的线程数超过CPU核数则加入队列
    			List<String[]> runArgs = FileHelper.readRunArgs("run.ini");
    			ExecutorService pool = Executors.newFixedThreadPool(runArgs.size());
        		for(String[] args : runArgs) {
        			CommandLine mcmd = CliHelper.parse(args);
        			pool.execute(new CmdProcessor(mcmd));
    			}
    		} else {
    			CmdProcessor cp = new CmdProcessor(cmd);
    			cp.process();
    		}
		} catch (ParseException e) {
			logger.error("解析命令行参数出错， 请输入help查看用法。", e);
		}
	}
	
	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

}
