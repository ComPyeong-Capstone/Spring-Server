    package com.example.AIVideoApp.entity;

    import com.example.AIVideoApp.entity.Post;
    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    @Entity
    @Getter
    @Setter
    @NoArgsConstructor
    @Table(name = "post_image")
    public class PostImage {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long imageId;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "postId")
        private Post post;

        private String imageURL;
    }
