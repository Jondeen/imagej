/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imagej.envisaje.imageexplorer;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JScrollPane;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.util.ImageUtilities;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.PasteAction;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//imagej.envisaje.imageexplorer//ImgExplorer//EN",
autostore = false)
public final class ImgExplorerTopComponent extends TopComponent implements ExplorerManager.Provider {

    private static ImgExplorerTopComponent instance;
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "imagej/envisaje/imageexplorer/folder_image.png";
    private static final String PREFERRED_ID = "ImgExplorerTopComponent";
    ExplorerManager mgr = new ExplorerManager();
    //FileObject root = FileUtil.toFileObject(new File("C:/Documents and Settings/"
    //        + getUsername() + "/My Documents/My Pictures"));
    FileObject root = FileUtil.toFileObject(
            //new File("C:/Users/" + java.lang.System.getProperty("user.name") + "/Pictures"));
            new File("C:/"));
    private String username;
    private DataObject dataObject;
    private Node rootnode;

    public ImgExplorerTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(ImgExplorerTopComponent.class, "CTL_ImgExplorerTopComponent"));
        setToolTipText(NbBundle.getMessage(ImgExplorerTopComponent.class, "HINT_ImgExplorerTopComponent"));
        setIcon(ImageUtilities.loadImage(ICON_PATH, true));

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new BeanTreeView();

        setLayout(new java.awt.BorderLayout());
        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized ImgExplorerTopComponent getDefault() {
        if (instance == null) {
            instance = new ImgExplorerTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the ImgExplorerTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized ImgExplorerTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(ImgExplorerTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof ImgExplorerTopComponent) {
            return (ImgExplorerTopComponent) win;
        }
        Logger.getLogger(ImgExplorerTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        setData();
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    Object readProperties(java.util.Properties p) {
        if (instance == null) {
            instance = this;
        }
        instance.readPropertiesImpl(p);
        return instance;
    }

    private void readPropertiesImpl(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    void setData() {
//        root = FileUtil.toFileObject(new File("C:/Documents and Settings/"
//                + getUsername() + "/My Documents/My Pictures"));
        if (root != null) {
            try {
                dataObject = DataObject.find(root);
                rootnode = dataObject.getNodeDelegate();
                mgr.setRootContext(rootnode);
            } catch (DataObjectNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getUsername() {
        username = java.lang.System.getProperty("user.name");
        return username;
    }
}

//public final class ExplorerTopComponent extends TopComponent
//        implements ExplorerManager.Provider {
//
//    private static final String ROOT_NODE = "Explorer";
//    private final ExplorerManager manager = new ExplorerManager();
//
//    private ExplorerTopComponent() {
//        initComponents();
//        initTree();
//        initActions();
//        associateLookup(ExplorerUtils.createLookup(manager, getActionMap()));
//    }
//    private JScrollPane jScrollPane1;
//
//    private void initComponents() {
//        jScrollPane1 = new BeanTreeView();
//        setLayout(new BorderLayout());
//        add(jScrollPane1, BorderLayout.CENTER);
//    }
//
//    private void initTree() {
//        FileObject folder = Repository.getDefault().
//                getDefaultFileSystem().findResource(ROOT_NODE);
//        if (folder != null) { /* folder found */
//            manager.setRootContext(new ExplorerFolderNode(folder));
//        }
//    }
//
//    private void initActions() {
//        CutAction cut = SystemAction.get(CutAction.class);
//        getActionMap().put(cut.getActionMapKey(),
//                ExplorerUtils.actionCut(manager));
//        CopyAction copy = SystemAction.get(CopyAction.class);
//        getActionMap().put(copy.getActionMapKey(),
//                ExplorerUtils.actionCopy(manager));
//        PasteAction paste = SystemAction.get(PasteAction.class);
//        getActionMap().put(paste.getActionMapKey(),
//                ExplorerUtils.actionPaste(manager));
//        DeleteAction delete = SystemAction.get(DeleteAction.class);
//        getActionMap().put(delete.getActionMapKey(),
//                ExplorerUtils.actionDelete(manager, true));
//    }
//
//    public ExplorerManager getExplorerManager() {
//        return manager;
//    }
//
//    protected void componentActivated() {
//        ExplorerUtils.activateActions(manager, true);
//    }
//
//    protected void componentDeactivated() {
//        ExplorerUtils.activateActions(manager, false);
//    }
//    private List<Action> ca = null;
//
//    @Override
//    public Action[] getActions() {
//        if (ca == null) {
//            ca = new ArrayList<Action>(Arrays.asList(super.getActions()));
//            ca.add(null); /* add separator */
//            Lookup lkp = Lookups.forPath("ContextActions/MyTC");
//            ca.addAll(lkp.lookupAll(Action.class));
//        }
//        return ca.toArray(new Action[ca.size()]);
//    }


    /* The actions are declared in the layer file and read on demand in the getActions() method.
     * Firstly, the superclasss getActions() method is called to obtain default actions. With the
help of the method Lookups.forPath(), a Lookup for the declared folder ContextActions/MyTC
is created. The method lookupAll() then obtains all registered actions implementing the
Action interface. When creating the menu, a null value is automatically replaced by a separator
in the platform. The assembled list is returned as an array. The entry with the declared
folder in the layer file looks like this:

<folder name="ContextActions">
  <folder name="MyTC">
    <file name="MyAction1.shadow">
      <attr name="originalFile"
         stringvalue="Actions/Edit/com-galileo-netbeans-module-MyAction1.instance"/>
    </file>
    <file name="MyAction2.shadow">
      <attr name="originalFile"
         stringvalue="Actions/Edit/com-galileo-netbeans-module-MyAction2.instance"/>
    </file>
  </folder>
</folder>
     *
     */
//}