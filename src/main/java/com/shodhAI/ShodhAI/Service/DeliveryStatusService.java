package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.DeliveryStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryStatusService
{
    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    public DeliveryStatus getDeliveryStatusById(Long deliveryStatusId) throws Exception {
        try {

            TypedQuery<DeliveryStatus> query = entityManager.createQuery(Constant.GET_DELIVERY_STATUS_BY_ID, DeliveryStatus.class);
            query.setParameter("deliveryStatusId", deliveryStatusId);
            return query.getResultList().get(0);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException(indexOutOfBoundsException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }
}
