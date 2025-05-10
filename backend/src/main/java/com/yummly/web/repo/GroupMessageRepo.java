package com.yummly.web.repo;

import com.yummly.web.model.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GroupMessageRepo extends JpaRepository<GroupMessage, Long> {
    List<GroupMessage> findByGroupIdOrderByCreatedAtDesc(Long groupId);
}
