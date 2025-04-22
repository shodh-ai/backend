package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shodhAI.ShodhAI.Utils.DocumentType;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;
@Builder
@Entity
@Table(name = "file_type")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_type_id")
    @JsonProperty("file_type_id")
    private Long fileTypeId;

    @Column(name = "file_type_name", unique = true)
    @JsonProperty("file_type_name")
    private String fileTypeName;

    @NonNull
    @Column(name = "archived")
    @JsonProperty("archived")
    private Character archived = 'N';

    public FileType(Long fileTypeId, String fileTypeName) {
        this.fileTypeId = fileTypeId;
        this.fileTypeName = fileTypeName;
    }

    @Nullable
    @ManyToMany(mappedBy = "requiredDocumentTypes")
    @JsonIgnore
    private List<DocumentType> documentTypes;

}