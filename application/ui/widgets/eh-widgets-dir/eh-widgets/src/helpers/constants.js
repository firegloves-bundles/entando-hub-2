
//Hub Roles
export const AUTHOR = 'eh-author'
export const MANAGER = 'eh-manager'
export const ADMIN = 'eh-admin'

//Bundle Status
export const BUNDLE_STATUS = {
    NOT_PUBLISHED: 'NOT_PUBLISHED',
    PUBLISHED: 'PUBLISHED',
    PUBLISH_REQ: 'PUBLISH_REQ',
    DELETE_REQ: 'DELETE_REQ',
}

// All Button Labels
export const BUTTON_LABELS = {
    DELETE: "Delete",
    EDIT: "Edit",
    CANCEL: "Cancel",
}

// All Modal Labels
export const MODAL_LABELS = {
    DELETE_BUNDLE_MSG: "Are you sure you want to delete this bundle?",
}

// All API Response Key
export const API_RESPONSE_KEY = {
    EDITED_BUNDLE_GROUP : 'editedBundleGroup'
}

// Constant String
export const DELETED_BUNDLE = 'deletedBundle';
export const GIT_REPO = 'gitRepo';

// REGEX
export const DOCUMENTATION_ADDRESS_URL_REGEX = /[-a-zA-Z0-9@:%_+.~#?&//=]{2,256}\.[a-z]{2,4}\b(\/[-a-zA-Z0-9@:%_+.~#?&//=]*)?/gi
export const VERSON_REGEX = /^[v]?([0-9]|[1-9][0-9]*)\.([0-9]|[1-9][0-9]*)\.([0-9]|[1-9][0-9]*)(?:-([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?(?:\+([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?$/gm
export const BUNDLE_URL_REGEX = /^(https|git)(:\/\/|@)([^/:]+)[/:]([^/:]+)\/([a-z-A-Z-0-9/]+)(?:\.git)$/gm
