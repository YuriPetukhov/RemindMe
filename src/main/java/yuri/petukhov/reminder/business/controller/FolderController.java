package yuri.petukhov.reminder.business.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import yuri.petukhov.reminder.business.model.Folder;
import yuri.petukhov.reminder.business.service.FolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/folders")
@RequiredArgsConstructor
@Slf4j
public class FolderController {

    private final FolderService folderService;

    @GetMapping("/all")
    public ResponseEntity<List<Folder>> getAllFolders(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        List<Folder> folders = folderService.getAllFoldersByUserId(userId);
        return ResponseEntity.ok().body(folders);
    }

    @PostMapping("/create")
    public ResponseEntity<Folder> createFolder(@RequestBody Folder folder, @RequestParam(required = false) Long parentFolderId, Authentication authentication) {
        Folder createdFolder = folderService.createFolder(folder, parentFolderId, Long.valueOf(authentication.getName()));
        return ResponseEntity.ok().body(createdFolder);
    }

    @DeleteMapping("/delete/{folderId}")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long folderId) {
        folderService.deleteFolder(folderId);
        return ResponseEntity.noContent().build();
    }
}
