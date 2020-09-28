/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thespheres.sphairas.login.signee;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import org.thespheres.betula.document.Signee;

/**
 *
 * @author boris.heithecker
 */
@NamedQuery(name = "signeeEntity.findForAccount", query = "SELECT se FROM SigneeEntity se "
        + "WHERE se.account=:account",
        hints = {
            @QueryHint(name = "eclipselink.query-results-cache", value = "true"),
            @QueryHint(name = "eclipselink.query-results-cache.size", value = "250")
        })
@Entity
@Table(name = "SIGNEE")
@IdClass(Signee.class)
@Access(AccessType.FIELD)
public class SigneeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "SIGNEE_ID")
    private String prefix;
    @Id
    @Column(name = "SIGNEE_AUTHORITY", length = 64)
    private String suffix;
    @Id
    @Column(name = "SIGNEE_ALIAS")
    private boolean alias;
    @Column(name = "SIGNEE_FULLNAME")
    private String fullName;
    @Column(name = "SIGNEE_ACCOUNT")
    private String account;
    @Embedded
    @ElementCollection
    @CollectionTable(name = "SIGNEE_MARKERS")
    private Set<EmbeddableMarker> markerSet = new HashSet<>();
    @ElementCollection
    @MapKeyColumn(name = "PROPERTY_NAME")
    @Column(name = "PROPERTY_VALUE", length = 256)
    @CollectionTable(name = "SIGNEE_PROPERTIES")
    private Map<String, String> properties = new HashMap<>();
    @Column(name = "SIGNEE_GROUPS")
    private String groups;

    public SigneeEntity() {
    }

    public SigneeEntity(final Signee signee, final String commonName) {
        this.prefix = signee.getId();
        this.suffix = signee.getAuthority();
        this.alias = signee.isAlias();
        this.fullName = commonName;
    }

    public Signee getSignee() {
        return new Signee(prefix, suffix, alias);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(final String fn) {
        this.fullName = fn;
    }

    public String[] getGroups() {
        return Optional.ofNullable(groups)
                .map(s -> Arrays.stream(s.split(",")).filter(v -> !v.isBlank()))
                .map(s -> Stream.concat(Stream.of("signees"), s))
                .map(s -> s.toArray(String[]::new))
                .orElse(null);
    }

    public Set<EmbeddableMarker> getMarkerSet() {
        return markerSet;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.prefix);
        hash = 67 * hash + Objects.hashCode(this.suffix);
        return 67 * hash + (this.alias ? 1 : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SigneeEntity other = (SigneeEntity) obj;
        if (!Objects.equals(this.prefix, other.prefix)) {
            return false;
        }
        if (!Objects.equals(this.suffix, other.suffix)) {
            return false;
        }
        return this.alias == other.alias;
    }

    @Override
    public String toString() {
        return "org.thespheres.betula.entities.SigneeEntity[ id=" + prefix + " ]";
    }

}
