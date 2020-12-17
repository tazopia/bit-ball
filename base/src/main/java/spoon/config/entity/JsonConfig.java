package spoon.config.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@Data
@Entity
@Table(name = "JSON_CONFIG")
public class JsonConfig {

    @Id
    @Column(length = 32)
    private String code;

    @Column(nullable = false, columnDefinition = "nvarchar(max)")
    private String json;

    public JsonConfig(String code) {
        this.code = code;
    }

}
