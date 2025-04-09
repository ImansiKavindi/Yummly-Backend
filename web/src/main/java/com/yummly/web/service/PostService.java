package com.yummly.web.service;

import com.yummly.web.exception.FileStorageException;
import com.yummly.web.model.Post;
import com.yummly.web.model.User;
import com.yummly.web.repo.PostRepository;
import com.yummly.web.repo.UserRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepo userRepo; // ✅ Added

    @Value("${file.upload.dir}")
    private String uploadDir;

    public Post createPost(String title, String description, Long userId, MultipartFile image, MultipartFile video) {
        Post post = new Post();
        post.setTitle(title);
        post.setDescription(description);

        // ✅ Fetch full user from repository
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        post.setUser(user);

        // ✅ Handle image upload
        if (image != null && !image.isEmpty() && isValidImage(image.getContentType())) {
            try {
                String imageName = saveFile(image);
                post.setImagePath(imageName);
            } catch (Exception e) {
                logger.error("Error saving image file: " + e.getMessage());
                return null;
            }
        }

        // ✅ Handle video upload
        if (video != null && !video.isEmpty() && isValidVideo(video.getContentType())) {
            try {
                String videoName = saveFile(video);
                post.setVideoPath(videoName);
            } catch (Exception e) {
                logger.error("Error saving video file: " + e.getMessage());
                return null;
            }
        }

        // ✅ Save the post
        return postRepository.save(post);
    }

    private String saveFile(MultipartFile file) throws IOException {
        try {
            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = dirPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            return fileName;
        } catch (IOException e) {
            logger.error("File saving error: " + e.getMessage());
            throw e;
        }
    }

    private boolean isValidImage(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }

    private boolean isValidVideo(String contentType) {
        return contentType != null && contentType.startsWith("video/");
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public Post updatePost(Post post) {
        // Save the updated post, could be using a repository, like:
        return postRepository.save(post);
    }

    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByUser_Id(userId); // ✅ correct method name

    }


}
