package gzs.fiar.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "click")
public class Click {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "link")
    private String link;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "language")
    private String language;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "screen_size")
    private String screenSize;

}
