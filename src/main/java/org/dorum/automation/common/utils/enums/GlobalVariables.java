package org.dorum.automation.common.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GlobalVariables {

    BUILD_DEFINITION_NAME   ("Build.DefinitionName"),
    BUILD_NUMBER            ("BUILD_NUMBER"),
    BUILD_REPOSITORY_URI    ("Build.Repository.Uri"),
    BUILD_SOURCE_BRANCH     ("Build.SourceBranch"),
    BUILD_SOURCE_VERSION    ("Build.SourceVersion"),
    DB_PASS_WORD            ("DB_PASSWORD"),
    DB_USER                 ("DB_USER"),
    GIT_BRANCH              ("GIT_BRANCH"),
    GIT_COMMIT              ("GIT_COMMIT"),
    GIT_URL                 ("GIT_URL"),
    IS_AZURE_REPORTING      ("IS_AZURE_REPORTING"),
    JOB_NAME                ("JOB_NAME"),
    PASSWORD                ("password"),
    PERFECTO_COMMIT         ("perfecto.vcs.commit"),
    PERFECTO_FILE_PATH      ("perfecto.vcs.filePath"),
    PERFECTO_REPO_URL       ("perfecto.vcs.repositoryUrl"),
    RELEASE_RELEASE_ID      ("Release.ReleaseId"),
    USER_NAME               ("username"),
    SYSTEM_USER_NAME        ("user.name");

    private final String value;
}
