package com.learn.lld;

import java.util.*;

/**
 * Day 14 Practice Problem — Menu System
 *
 * Problem: MenuManager was a broken Singleton, had if-else on menu type
 * ("ITEM"/"SUBMENU") that only handled 2 levels of nesting, public fields,
 * device-type if-else, and a fat MenuRenderer interface.
 *
 * Concepts tested: Composite (infinite menu nesting), Strategy (device),
 * Factory (device), SRP, DIP, Singleton issues, Encapsulation, Value Object
 *
 * KEY PATTERN: Composite — the menu is a tree (items + submenus that contain
 * items + submenus). Composite handles infinite nesting and eliminates the
 * "type" string + if-else checking.
 */

// ══════════ VALUE OBJECT (replaces long param list) ══════════

class MenuSettings {
    private final String userRole;
    private final String deviceType;
    private final boolean darkMode;
    private final String language;

    MenuSettings(String userRole, String deviceType, boolean darkMode, String language) {
        this.userRole = userRole;
        this.deviceType = deviceType;
        this.darkMode = darkMode;
        this.language = language;
    }

    String getUserRole() { return userRole; }
    String getDeviceType() { return deviceType; }
    boolean isDarkMode() { return darkMode; }
    String getLanguage() { return language; }
}

// ══════════ STRATEGY + FACTORY — Device rendering ══════════

interface DeviceRenderer {
    void print(String label);
}

class MobileRenderer implements DeviceRenderer {
    public void print(String label) { System.out.println("[Mobile] " + label); }
}

class DesktopRenderer implements DeviceRenderer {
    public void print(String label) { System.out.println("[Desktop] " + label); }
}

class DeviceRendererFactory {
    static DeviceRenderer create(String deviceType) {
        return switch (deviceType) {
            case "MOBILE" -> new MobileRenderer();
            case "DESKTOP" -> new DesktopRenderer();
            default -> throw new IllegalArgumentException("Unknown device: " + deviceType);
        };
    }
}

// ══════════ COMPOSITE — Menu tree (infinite nesting) ══════════

// COMPONENT — common interface for leaf (item) and composite (group)
interface MenuEntry {
    void render(MenuSettings settings, DeviceRenderer renderer);
    int countItems();
}

// LEAF — a menu item (no children)
class MenuItem implements MenuEntry {
    private final String label;
    private final String requiredRole;
    private final String action;

    MenuItem(String label, String requiredRole, String action) {
        this.label = label;
        this.requiredRole = requiredRole;
        this.action = action;
    }

    @Override
    public void render(MenuSettings settings, DeviceRenderer renderer) {
        if (requiredRole == null || requiredRole.equals(settings.getUserRole())) {
            renderer.print(label);
        }
    }

    @Override
    public int countItems() {
        return 1;
    }
}

// COMPOSITE — a submenu (contains MenuEntry: items OR submenus, recursively)
class MenuGroup implements MenuEntry {
    private final String label;
    private final String requiredRole;
    private final List<MenuEntry> children = new ArrayList<>();

    MenuGroup(String label, String requiredRole) {
        this.label = label;
        this.requiredRole = requiredRole;
    }

    public void add(MenuEntry entry) { children.add(entry); }

    @Override
    public void render(MenuSettings settings, DeviceRenderer renderer) {
        if (requiredRole != null && !requiredRole.equals(settings.getUserRole())) {
            return;  // role check for the whole group
        }
        renderer.print(label + " (submenu)");
        // Recurse — each child renders itself (item OR nested group)
        children.forEach(child -> child.render(settings, renderer));
    }

    @Override
    public int countItems() {
        // 1 (this group) + recursive count of all children
        return 1 + children.stream().mapToInt(MenuEntry::countItems).sum();
    }
}

// ══════════ MENU SERVICE — orchestrator (SRP, DIP) ══════════

class MenuService {
    private final MenuEntry rootMenu;

    MenuService(MenuEntry rootMenu) {
        this.rootMenu = rootMenu;
    }

    public void render(MenuSettings settings) {
        DeviceRenderer renderer = DeviceRendererFactory.create(settings.getDeviceType());
        rootMenu.render(settings, renderer);
    }

    public int countItems() {
        return rootMenu.countItems();
    }
}

// ══════════ ADAPTER — GoogleAnalyticsSDK (if analytics needed) ══════════

interface MenuAnalytics {
    void trackClick(String menuLabel, String userId);
}

class GoogleAnalyticsAdapter implements MenuAnalytics {
    private final GoogleAnalyticsSDK sdk;

    GoogleAnalyticsAdapter(GoogleAnalyticsSDK sdk) { this.sdk = sdk; }

    public void trackClick(String menuLabel, String userId) {
        sdk.trackMenuClick(menuLabel, userId);  // translate to SDK's method
    }
}

// ══════════ USAGE ══════════

class Day14FacadeComposite {
    public static void main(String[] args) {
        // Build a menu tree with infinite nesting
        MenuGroup root = new MenuGroup("Main", null);
        root.add(new MenuItem("Home", null, "/home"));
        root.add(new MenuItem("Profile", "USER", "/profile"));

        MenuGroup settings = new MenuGroup("Settings", "ADMIN");
        settings.add(new MenuItem("Users", "ADMIN", "/settings/users"));

        MenuGroup advanced = new MenuGroup("Advanced", "ADMIN");  // nested submenu!
        advanced.add(new MenuItem("Logs", "ADMIN", "/settings/advanced/logs"));
        settings.add(advanced);  // submenu inside submenu — Composite handles it

        root.add(settings);

        // Render for admin on mobile
        MenuService menuService = new MenuService(root);
        MenuSettings userSettings = new MenuSettings("ADMIN", "MOBILE", true, "en");
        menuService.render(userSettings);

        System.out.println("Total items: " + menuService.countItems());
    }
}
