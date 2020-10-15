/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thespheres.sphairas.login.signee;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import org.thespheres.betula.document.Signee;

/**
 *
 * @author boris.heithecker@gmx.net
 */
@Dependent
public class SigneeManager {

    @PersistenceContext(unitName = "db")
    protected EntityManager em;

    public SigneeItem findSigneeItem(final Signee id) {
        final SigneeEntity s = em.find(SigneeEntity.class, id, LockModeType.OPTIMISTIC);
        return s != null ? new SigneeItem(s) : null;
    }

    public SigneeItem[] findAllSigneeItems() {
        final javax.persistence.criteria.CriteriaQuery<SigneeEntity> cq = em.getCriteriaBuilder().createQuery(SigneeEntity.class);
        cq.select(cq.from(SigneeEntity.class)).distinct(true);
        return em.createQuery(cq).setLockMode(LockModeType.OPTIMISTIC).getResultList().stream().map(SigneeItem::new).toArray(SigneeItem[]::new);
    }

    void updateSigneeItem(final Signee signee, final SigneeItem item) {
        SigneeEntity entity = em.find(SigneeEntity.class, signee, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        if (entity == null) {
            entity = new SigneeEntity(signee);
        }
        if (item.getAccount() != null && !item.getAccount().isBlank()) {
            entity.setAccount(item.getAccount());
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            entity.setFullName(item.getName());
        }
        if (item.getGroups() != null) {
            entity.setGroups(item.getGroups());
        }
        if (item.getProperties() != null) {
            entity.getProperties().clear();
            entity.getProperties().putAll(item.getProperties());
        }
    }

    public String getSigneeName(final String account) {
        return findSigneeEntity(account)
                .map(SigneeEntity::getFullName)
                .orElse(account);
    }

    public String[] getSigneeGroups(final String account) {
        return findSigneeEntity(account)
                .map(SigneeEntity::getGroups)
                .orElse(new String[0]);
    }

    public Map<String, String> getSigneeProperties(final String account) {
        return findSigneeEntity(account)
                .map(SigneeEntity::getProperties)
                .orElse(Collections.EMPTY_MAP);
    }

    private Optional<SigneeEntity> findSigneeEntity(final String account) {
        try {
            final SigneeEntity ret = em.createNamedQuery("signeeEntity.findForAccount", SigneeEntity.class)
                    .setParameter("account", account)
                    .getSingleResult();
            return Optional.of(ret);
        } catch (final NoResultException nex) {
        } catch (final NonUniqueResultException nuex) {
            Logger.getLogger(SigneeManager.class.getCanonicalName()).log(Level.SEVERE, "Multiple signees for " + account, nuex);
        }
        return Optional.empty();
    }

}
