package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Dto.VisualizationDto;
import com.shodhAI.ShodhAI.Entity.Edge;
import com.shodhAI.ShodhAI.Entity.Node;
import com.shodhAI.ShodhAI.Entity.Topic;
import com.shodhAI.ShodhAI.Service.EdgeService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.NodeService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.Service.TopicService;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/visualization", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class Visualization {

    @Autowired
    TopicService topicService;

    @Autowired
    EdgeService edgeService;

    @Autowired
    NodeService nodeService;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @GetMapping(value = "/get/{topicIdString}")
    public ResponseEntity<?> getVisualization(HttpServletRequest request, @PathVariable String topicIdString) {
        try {

            Long topicId = Long.parseLong(topicIdString);
            Topic topic = topicService.getTopicById(topicId);

            List<Node> nodes = nodeService.getNodeByTopic(topic);
            List<Edge> edges = edgeService.getEdgeByTopic(topic);

            VisualizationDto visualizationDto = new VisualizationDto();
            visualizationDto.setEdges(edges);
            visualizationDto.setNodes(nodes);
            visualizationDto.setTopic(topic.getTopicTitle());
            visualizationDto.setJsxCode(topic.getJsxCode());
            visualizationDto.setNarration(topic.getNarration());

            return ResponseService.generateSuccessResponse("Visualization Fetched Successfully", visualizationDto, HttpStatus.OK);

        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            exceptionHandlingService.handleException(dataIntegrityViolationException);
            throw new IndexOutOfBoundsException("Data Integrity Exception caught: " + dataIntegrityViolationException.getMessage());
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            return ResponseService.generateErrorResponse("Index Out of Bound Exception Caught: " + indexOutOfBoundsException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            return ResponseService.generateErrorResponse("Persistence Exception Caught: " + persistenceException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse("Illegal Exception Caught: " + illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
