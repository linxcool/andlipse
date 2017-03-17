package com.linxcool.andlipse.core;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * 控制台管理器
 * @author huchanghai
 */
public class LConsole implements IConsoleFactory {

	static MessageConsole console = new MessageConsole("", null);
	static boolean exists = false;

	@Override
	@Deprecated
	public void openConsole() {
		show();
	}

	public static void show() {
		if (console == null) {
			return;
		}
		
		// 得到默认控制台管理器
		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
		
		// 得到所有的控制台实例
		IConsole[] existing = manager.getConsoles();
		exists = false;
		// 新创建的MessageConsole实例不存在就加入到控制台管理器，并显示出来
		for (int i = 0; i < existing.length; i++) {
			if (console == existing[i])
				exists = true;
		}
		if (!exists) {
			manager.addConsoles(new IConsole[] { console });
		}
	}

	public static void close() {
		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
		if (console != null) {
			manager.removeConsoles(new IConsole[] { console });
		}
	}

	public static MessageConsole get() {
		show();
		return console;
	}

	public static void print(String message) {
		print(message, true);
	}

	/**
	 * 向控制台打印一条信息，并激活控制台。
	 * @param message
	 * @param activate 是否激活控制台
	 */
	public static void print(String message, boolean activate) {
		MessageConsoleStream printer = LConsole.get().newMessageStream();
		printer.setActivateOnWrite(activate);
		printer.println(message);
	}

}
