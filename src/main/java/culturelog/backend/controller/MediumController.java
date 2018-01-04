package culturelog.backend.controller;

import culturelog.backend.domain.Medium;
import culturelog.backend.domain.User;
import culturelog.backend.dto.MediumDto;
import culturelog.backend.exception.CultureLogControllerExceptionKey;
import culturelog.backend.exception.CultureLogException;
import culturelog.backend.exception.CultureLogExceptionKey;
import culturelog.backend.security.CultureLogSecurityService;
import culturelog.backend.service.MediumService;
import culturelog.backend.service.MessageService;
import culturelog.backend.utils.MediumUtils;
import culturelog.backend.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

/**
 * @author Jan Venstermans
 */
@RestController
@RequestMapping("/media")
public class MediumController {

    @Autowired
    private MediumService mediumService;

    @Autowired
    private CultureLogSecurityService securityService;

    @Autowired
    private MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createMedium(@RequestBody MediumDto mediumDto, Locale locale) {
        Medium medium = MediumUtils.fromMediumDto(mediumDto);
        try {
            if (medium.getId() != null) {
                throw new CultureLogException(CultureLogExceptionKey.CREATE_WITH_ID);
            }
            medium.setUser(securityService.getLoggedInUser());

            Medium newMedium = mediumService.save(medium);
            return ResponseEntity.status(HttpStatus.CREATED).body(MediumUtils.toMediumDto(newMedium));
        } catch (CultureLogException e) {
            return ResponseEntity.badRequest().body(messageService.getControllerMessage(CultureLogControllerExceptionKey.MEDIA_CREATE, e, locale));
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMediaOfUserAndGlobalMedia() {
        Long userId = securityService.getLoggedInUserId();
        List<Medium> mediumList = mediumService.getMediaOfUserByUserId(userId, true);
        return ResponseEntity.ok(MediumUtils.toMediumDtoList(mediumList));
    }

    @RequestMapping(value = "/{mediumId}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMedium(@PathVariable(value="mediumId") Long mediumId, Locale locale) {
        User user = securityService.getLoggedInUser();
        Medium medium = mediumService.getById(mediumId);
        if (medium != null && MediumUtils.isMediumOfUser(medium, user, true)) {
            return ResponseEntity.ok(MediumUtils.toMediumDto(medium));
        }
        return ResponseEntity.badRequest().body(messageService.getControllerMessage(
                CultureLogControllerExceptionKey.MEDIA_GET_ONE_UNKNOWN_ID, new Object[]{mediumId}, locale));
    }

    @RequestMapping(value = "/{mediumId}", method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateMedium(@PathVariable(value="mediumId") Long mediumId, @RequestBody MediumDto mediumDto, Locale locale) {
        Medium medium = MediumUtils.fromMediumDto(mediumDto);
        User user = securityService.getLoggedInUser();
        Medium existingMedium = mediumService.getById(mediumId);
        if (existingMedium == null || !UserUtils.areUsersSame(existingMedium.getUser(), user)) {
            return ResponseEntity.badRequest().body(messageService.getControllerMessage(
                    CultureLogControllerExceptionKey.MEDIA_UPDATE_ONE, new Object[]{mediumId}, locale));
        }
        medium.setId(mediumId);
        medium.setUser(user);
        try {
            Medium updatedMedium = mediumService.save(medium);
            return ResponseEntity.ok(MediumUtils.toMediumDto(updatedMedium));
        } catch (CultureLogException e) {
            return ResponseEntity.badRequest().body(messageService.getControllerMessage(
                    CultureLogControllerExceptionKey.MEDIA_UPDATE_ONE, new Object[]{mediumId}, e, locale));
        }
    }

    //TODO: remove medium, with check if used in experience
}