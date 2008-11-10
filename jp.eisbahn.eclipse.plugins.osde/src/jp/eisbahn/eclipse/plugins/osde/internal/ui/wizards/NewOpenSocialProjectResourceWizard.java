package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.StatusUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

/**
 * OpenSocialプロジェクトを作成するためのウィザードクラスです。
 * @author yoichiro
 */
public class NewOpenSocialProjectResourceWizard extends BasicNewResourceWizard implements IExecutableExtension {

	/** ウィザードID */
	public static final String WIZARD_ID = "jp.eisbahn.eclipse.plugins.osde.ui.wizards.new.project";
	
	/** プロジェクト作成ページ */
	private  WizardNewProjectCreationPage mainPage;
	
	/** 新しく作成するプロジェクトのキャッシュ */
	private IProject newProject;
	
	/** このウィザードを宣言する設定要素 */
//	private IConfigurationElement configElement;
	
	/**
	 * このオブジェクトが生成されるときに呼び出されます。
	 */
	public NewOpenSocialProjectResourceWizard() {
		super();
		// このプラグインのためのダイアログ設定の取得
		IDialogSettings workbenchSetting = Activator.getDefault().getDialogSettings();
		// このウィザードのためのダイアログ設定を取得
		IDialogSettings section = workbenchSetting.getSection("NewOpenSocialProjectResourceWizard");
		// ダイアログ設定がまだないかチェック
		if (section == null) {
			// ダイアログ設定を新たに追加
			section = workbenchSetting.addNewSection("NewOpenSocialProjectResourceWizard");
		}
		// このウィザードのためのダイアログ設定を割り当てる
		setDialogSettings(section);
	}
	
	/**
	 * 各ページを追加します。
	 */
	public void addPages() {
		super.addPages();
		// プロジェクト作成ページを追加
		mainPage = new WizardNewProjectCreationPage("basicNewProjectPage");
		mainPage.setTitle("OpenSocial Project");
		mainPage.setDescription("Create a new OpenSocial project resource.");
		addPage(mainPage);
	}
	
	/**
	 * このウィザードを初期化します。
	 * @param workbench ワークベンチオブジェクト
	 * @param currentSelection このウィザードが起動されたときの選択状況
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		super.init(workbench, currentSelection);
		setNeedsProgressMonitor(true);
		setWindowTitle("New OpenSocial project");
	}
	
	/**
	 * 作成された新しいプロジェクトを返します。
	 * @return 作成された新しいプロジェクト
	 */
	public IProject getNewProject() {
		return newProject;
	}

	/**
	 * このウィザードが完了した際に呼び出されます。
	 * @return 完了処理が正常に行われた場合はtrue
	 */
	@Override
	public boolean performFinish() {
		// プロジェクトを作成
		createNewProject();
		// プロジェクトが作成されたかチェック
		if (newProject == null) {
			return false;
		}
		// 作成されたプロジェクトを選択
		selectAndReveal(newProject);
		return true;
	}
	
	/**
	 * 新しくプロジェクトを作成します。
	 * @return 作成されたOpenSocialプロジェクト
	 */
	private IProject createNewProject() {
		// 既にプロジェクトが作成されているかチェック
		if (newProject != null) {
			return newProject;
		}
		// プロジェクトハンドルを取得
		final IProject newProjectHandle = mainPage.getProjectHandle();
		// プロジェクト作成ジョブを作成
		IRunnableWithProgress op = new IRunnableWithProgress() {
			/**
			 * プロジェクトを作成する処理を行います。
			 * @param monitor 進捗報告モニタ
			 * @throws InvocationTargetException プロジェクト作成時に何らかの例外が発生したとき
			 */
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					// プロジェクトの作成
					newProjectHandle.create(monitor);
					newProjectHandle.open(monitor);
					// TODO Gadget XMLファイルの作成
					IFile gadgetXmlFile = newProjectHandle.getFile(new Path("gadget.xml"));
					String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
					content += "<Module>\n";
					content += "</Module>";
					InputStream in = new ByteArrayInputStream(content.getBytes("UTF8"));
					gadgetXmlFile.create(in, false, monitor);
				} catch(CoreException e) {
					throw new InvocationTargetException(e);
				} catch(UnsupportedEncodingException e) {
					throw new InvocationTargetException(e);
				}
			}
		};
		// プロジェクト作成ジョブを実行
		try {
			getContainer().run(true, true, op);
		} catch(InterruptedException e) {
			return null;
		} catch(InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if (t.getCause() instanceof CoreException) {
				CoreException cause = (CoreException)t.getCause();
				StatusAdapter status = new StatusAdapter(StatusUtil.newStatus(cause.getStatus().getSeverity(), "プロジェクト作成時にエラーが発生しました。", cause));
				status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, "プロジェクト作成時にエラーが発生しました。");
				StatusManager.getManager().handle(status, StatusManager.BLOCK);
			} else {
				StatusAdapter status = new StatusAdapter(StatusUtil.newStatus(IStatus.WARNING, "プロジェクト作成時にエラーが発生しました。", t));
				status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, "プロジェクト作成時にエラーが発生しました。");
				StatusManager.getManager().handle(status, StatusManager.BLOCK);
			}
			return null;
		}
		newProject = newProjectHandle;
		return newProject;
	}

	/**
	 * 初期化データをセットします。
	 * @param config 設定要素
	 * @param propertyName プロパティ名
	 * @param data 初期化データ
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) {
	}

}
