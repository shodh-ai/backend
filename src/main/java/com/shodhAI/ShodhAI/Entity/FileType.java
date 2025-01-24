package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shodhAI.ShodhAI.Utils.DocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "file_type")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileType
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_type_id")
    @JsonProperty("role_type_id")
    private Long fileTypeId;

    @Column(name = "file_type_name")
    @JsonProperty("role_type_id")
    private String fileTypeName;

    public FileType(Long fileTypeId, String fileTypeName) {
        this.fileTypeId = fileTypeId;
        this.fileTypeName = fileTypeName;
    }

    @ManyToMany(mappedBy = "required_document_types")
    @JsonIgnore
    private List<DocumentType> documentTypes;

}