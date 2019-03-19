package org.sunbird.keycloak.utils;

import java.util.List;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ModelDuplicateException;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.sunbird.keycloak.resetcredential.sms.KeycloakSmsAuthenticatorConstants;

/**
 * 
 * @author Amit Kumar
 * 
 *         Utility class for keycloak, it contains all the common method used across the
 *         application.
 * 
 */
public class SunbirdModelUtils {

  private static Logger logger = Logger.getLogger(SunbirdModelUtils.class);

  private SunbirdModelUtils() {}

  public static UserModel getUserByNameEmailOrPhone(AuthenticationFlowContext context,
      String username) {
    String numberRegex = "\\d+";
    KeycloakSession session = context.getSession();
    logger.debug("SunbirdModelUtils@getUser " + username);
    if (username.matches(numberRegex)) {
      List<UserModel> userModels = session.users().searchForUserByUserAttribute(
          KeycloakSmsAuthenticatorConstants.ATTR_MOBILE, username, context.getRealm());
      if (userModels != null && !userModels.isEmpty()) {
        // multiple user found for same attribute
        if (userModels.size() > 1) {
          throw new ModelDuplicateException(Constants.MULTIPLE_USER_ASSOCIATED_WITH_PHONE,
              KeycloakSmsAuthenticatorConstants.ATTR_MOBILE);
        }
        return userModels.get(0);
      } else {
        return KeycloakModelUtils.findUserByNameOrEmail(context.getSession(), context.getRealm(),
            username);
      }
    } else {
      return KeycloakModelUtils.findUserByNameOrEmail(context.getSession(), context.getRealm(),
          username);
    }
  }

}
