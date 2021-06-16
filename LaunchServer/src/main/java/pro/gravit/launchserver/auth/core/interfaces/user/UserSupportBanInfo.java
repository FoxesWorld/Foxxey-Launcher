package pro.gravit.launchserver.auth.core.interfaces.user;

import java.time.LocalDateTime;

public interface UserSupportBanInfo {
    @SuppressWarnings("unused")
    UserBanInfo getBanInfo();

    interface UserBanInfo {
        String getId();

        @SuppressWarnings("unused")
        default String getReason() {
            return null;
        }

        @SuppressWarnings("unused")
        default String getModerator() {
            return null;
        }

        @SuppressWarnings("unused")
        default LocalDateTime getStartDate() {
            return null;
        }

        @SuppressWarnings("unused")
        default LocalDateTime getEndDate() {
            return null;
        }
    }
}
