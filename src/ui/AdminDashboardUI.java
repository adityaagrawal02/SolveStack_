package ui;

public class AdminDashboardUI {

    public void setVisible(boolean visible) {
        if (!visible) {
            return;
        }

        FxComponents.runFx(() -> {
            if (!isAuthorized()) {
                new LoginUI().setVisible(true);
                return;
            }
            DashboardRouter.openDashboard(FxSolveStackApp.getPrimaryStage(), "Admin");
        });
    }

    private boolean isAuthorized() {
        UserSession session = UserSession.getInstance();
        return session.isLoggedIn() && session.hasRole("Admin");
    }
}

