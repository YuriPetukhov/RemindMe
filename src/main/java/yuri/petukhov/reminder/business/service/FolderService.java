package yuri.petukhov.reminder.business.service;

import yuri.petukhov.reminder.business.model.Folder;

import java.util.List;

public interface FolderService {
    List<Folder> getAllFoldersByUserId(Long userId);

    Folder createFolder(Folder folder, Long parentFolderId, Long userId);

    void deleteFolder(Long folderId);
}
