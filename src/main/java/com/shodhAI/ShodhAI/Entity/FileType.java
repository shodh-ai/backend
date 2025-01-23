package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shodhAI.ShodhAI.Utils.DocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FileType
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer file_type_id;

    @Column(name = "file_type_name")
    private String file_type_name;

    public FileType(Integer file_type_id, String file_type_name) {
        this.file_type_id = file_type_id;
        this.file_type_name = file_type_name;
    }

    @ManyToMany(mappedBy = "required_document_types")
    @JsonIgnore
    private List<DocumentType> documentTypes;
}