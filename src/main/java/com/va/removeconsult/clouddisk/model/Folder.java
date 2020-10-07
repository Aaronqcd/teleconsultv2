package com.va.removeconsult.clouddisk.model;

import java.util.List;

public class Folder {
	private String folderId;
	private String folderName;
	private String folderCreationDate;
	private String folderCreator;
	private String folderParent;
	private int folderConstraint;
	private Integer folderUserId;
	private Long folderSize;

	private String text; 

	private String type; 

	private String icon; 

	private List<Folder> nodes; 

	private String id; 

	public String getFolderId() {
		return this.folderId;
	}

	public void setFolderId(final String folderId) {
		this.folderId = folderId;
	}

	public String getFolderName() {
		return this.folderName;
	}

	public void setFolderName(final String folderName) {
		this.folderName = folderName;
	}

	public String getFolderCreationDate() {
		return this.folderCreationDate;
	}

	public void setFolderCreationDate(final String folderCreationDate) {
		this.folderCreationDate = folderCreationDate;
	}

	public String getFolderCreator() {
		return this.folderCreator;
	}

	public void setFolderCreator(final String folderCreator) {
		this.folderCreator = folderCreator;
	}

	public String getFolderParent() {
		return this.folderParent;
	}

	public void setFolderParent(final String folderParent) {
		this.folderParent = folderParent;
	}

	public int getFolderConstraint() {
		return folderConstraint;
	}

	public void setFolderConstraint(int folderConstraint) {
		this.folderConstraint = folderConstraint;
	}

	public Integer getFolderUserId() {
		return folderUserId;
	}

	public void setFolderUserId(Integer folderUserId) {
		this.folderUserId = folderUserId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public List<Folder> getNodes() {
		return nodes;
	}

	public void setNodes(List<Folder> nodes) {
		this.nodes = nodes;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getFolderSize() {
		return folderSize;
	}

	public void setFolderSize(Long folderSize) {
		this.folderSize = folderSize;
	}	
}
