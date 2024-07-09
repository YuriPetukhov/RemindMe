package yuri.petukhov.reminder.business.service;

import yuri.petukhov.reminder.business.model.User;

import java.util.List;

public interface AdminService {
    List<User> findAdmins();
}
