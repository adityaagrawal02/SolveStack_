package ui;

public class CompanyDashboardUI {

    public void setVisible(boolean visible) {
        if (!visible) {
            return;
        }

        FxComponents.runFx(() -> {
            if (!isAuthorized()) {
                new LoginUI().setVisible(true);
                return;
            }
            DashboardRouter.openDashboard(FxSolveStackApp.getPrimaryStage(), "Company");
        });
    }

    private boolean isAuthorized() {
        UserSession session = UserSession.getInstance();
        return session.isLoggedIn() && session.hasRole("Company");
    }
}

