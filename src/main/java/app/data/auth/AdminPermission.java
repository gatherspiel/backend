package app.data.auth;

import app.data.ContentItem;

public record AdminPermission(AdminType adminType, ContentItem contentItem) {
}
