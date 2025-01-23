package com.shodhAI.ShodhAI.Utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shodhAI.ShodhAI.Entity.FileType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "document")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DocumentType {

    @Id
    @Column(name = "document_type_id")
    @JsonProperty("document_type_id")
    private Long id;

    @Column(name = "document_type_name")
    @JsonProperty("document_type_name")
    private String documentTypeName;

    @Column(name = "description")
    @JsonProperty("description")
    private String description;

    @Column(name = "is_issue_date_required", nullable = false)
    @JsonProperty("is_issue_date_required")
    private Boolean isIssueDateRequired = false;

    @Column(name = "is_expiration_date_required", nullable = false)
    @JsonProperty("is_expiration_date_required")
    private Boolean isExpirationDateRequired = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "document_file_types",
            joinColumns = @JoinColumn(name = "document_type_id"),
            inverseJoinColumns = @JoinColumn(name = "file_type_id")
    )
    @JsonProperty("document_file_types")
    private List<FileType> required_document_types;

    @Column(name = "max_document_size")
    @JsonProperty("max_document_size")
    private String maxDocumentSize;

    @Column(name = "min_document_size")
    @JsonProperty("min_document_size")
    private String minDocumentSize;

}
