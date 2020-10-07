package com.va.removeconsult.clouddisk.pojo;

import java.util.ArrayList;
import java.util.List;

import com.va.removeconsult.clouddisk.model.Folder;

public class FolderTree {
	
	private List<Folder> FolderList = new ArrayList<Folder>();
    public FolderTree(List<Folder> FolderList) {
        this.FolderList=FolderList;
    }

    
    public List<Folder> builTree(String root){
        List<Folder> treeFolders =new  ArrayList<Folder>();
        for(Folder folderNode : getRootNode(root)) {
        	folderNode.setId(folderNode.getFolderId());
        	folderNode.setText(folderNode.getFolderName());
        	folderNode.setIcon("layui-icon layui-icon-dialogue icon-meeting");
        	folderNode.setType("all");
            folderNode=buildChilTree(folderNode);
            treeFolders.add(folderNode);
        }
        return treeFolders;
    }

    
    private Folder buildChilTree(Folder pNode){
        List<Folder> chilFolders =new  ArrayList<Folder>();
        for(Folder folderNode : FolderList) {
        	folderNode.setText(folderNode.getFolderName());
        	folderNode.setIcon("glyphicon glyphicon-folder-close");
        	folderNode.setType("children");
        	folderNode.setId(folderNode.getFolderId());
            if(folderNode.getFolderParent().equals(pNode.getFolderId())) {
                chilFolders.add(buildChilTree(folderNode));
            }
        }
        pNode.setNodes(chilFolders);
        return pNode;
    }

    
    private List<Folder> getRootNode(String root) {         
        List<Folder> rootFolderLists =new  ArrayList<Folder>();
        for(Folder FolderNode : FolderList) {
            if(FolderNode.getFolderParent().equals(root)) {
                rootFolderLists.add(FolderNode);
            }
        }
        return rootFolderLists;
    }

}
