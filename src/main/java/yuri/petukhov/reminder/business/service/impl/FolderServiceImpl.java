package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.model.Folder;
import yuri.petukhov.reminder.business.model.User;
import yuri.petukhov.reminder.business.repository.FolderRepository;
import yuri.petukhov.reminder.business.service.FolderService;
import yuri.petukhov.reminder.business.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;
    private final UserService userService;


    @Override
    public List<Folder> getAllFoldersByUserId(Long userId) {
        return folderRepository.findByUserId(userId);
    }

    @Override
    public Folder createFolder(Folder folder, Long parentFolderId, Long userId) {
        User user = userService.findUserById(userId);
        folder.setUser(user);
        if (parentFolderId != null) {
            Optional<Folder> parentFolderOpt = folderRepository.findById(parentFolderId);
            parentFolderOpt.ifPresent(folder::setParentFolder);
        }
        return folderRepository.save(folder);
    }

    @Override
    public void deleteFolder(Long folderId) {
        folderRepository.deleteById(folderId);
    }
}
