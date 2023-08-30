<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<style type="text/css">
    /*base css*/
    body {
        margin: 0px;
        padding: 15px;
    }

    body,
    td,
    th {
        font-family: "Lucida Grande", "Lucida Sans Unicode", Helvetica, Arial, Tahoma, sans-serif;
        font-size: 10pt;
    }

    th {
        text-align: left;
    }

    h1 {
        margin-top: 0px;
    }

    a {
        color: #4a72af
    }

    /*test results colors*/
    .total {
        color: #0000FF;
    }
    .failed {
        color: #ff4d4d;
    }

    .passed {
        color: #4caf50;
    }

    .skipped {
        color: #808080;
    }

    .broken {
        color: #ffff4d;
    }

    /*div styles*/
    .status {
        background-color: <%=build.result.toString()=="SUCCESS" ? 'green': (build.result.toString()=="UNSTABLE" ? 'yellow' : 'red') %>;
        font-size: 28px;
        font-weight: bold;
        color: white;
        width: 720px;
        height: 52px;
        margin-bottom: 18px;
        text-align: center;
        vertical-align: middle;
        border-collapse: collapse;
        background-repeat: no-repeat
    }

    .status .info {
        color: white !important;
        text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.3);
        font-size: 32px;
        line-height: 36px;
        padding: 8px 0
    }
</style>

<body>
    <div class="content round_border">
        <div class="status">
            <p class="info">The build is <%= build.result.toString().toLowerCase() %>
            </p>
        </div>
        <!-- status -->
        <table>
            <tbody>
                <tr>
                    <th>Project:</th>
                    <td>${project.name}</td>
                </tr>
                <tr>
                    <th>Build ${build.displayName}:</th>
                    <td><a href="${rooturl}${build.url}">${rooturl}${build.url}</a>
                    </td>
                </tr>
                <tr>
                    <th>Date of build:</th>
                    <td>${it.timestampString}</td>
                </tr>
                <tr>
                    <th>Build duration:</th>
                    <td>${build.durationString}</td>
                </tr>
                <tr>
                    <td colspan="2">&nbsp;</td>
                </tr>
            </tbody>
        </table>
        <!-- main -->
        <% def artifacts=build.artifacts %>
            <% if (artifacts !=null && artifacts.size()> 0) { %>
                <b>Build Artifacts:</b>
                <ul>
                    <% artifacts.each() { f -> %>
                        <li><a href="${rooturl}${build.url}artifact/${f}">${f}</a>
                        </li>
                        <% } %>
                </ul>
                <% } %>
                    <!-- artifacts -->
                    <% allureReportAction=build.getAction(ru.yandex.qatools.allure.jenkins.AllureReportBuildAction.class)
                            def passedCount = allureReportAction.getPassedCount()
                            def totalCount = allureReportAction.getTotalCount()
                            def successRate = (passedCount * 100f) / totalCount
                            def allureLastBuildSuccessRate = String.format("%.2f", successRate)
                            if (successRate < 50) {
                                allureLastBuildSuccessRate = "<span style='color: red;'>${allureLastBuildSuccessRate}</span>"
                            } else if (successRate >= 50 && successRate <= 90) {
                                allureLastBuildSuccessRate = "<span style='color: yellow;'>${allureLastBuildSuccessRate}</span>"
                            } else {
                                allureLastBuildSuccessRate = "<span style='color: green;'>${allureLastBuildSuccessRate}</span>"
                            }
                        def envVariableMap=it.getAction("org.jenkinsci.plugins.workflow.cps.EnvActionImpl").getEnvironment()
                        def perfectoURL=envVariableMap.PERFECTO_REPORT_URL
                        def azure=envVariableMap.AZURE_REPORT_FILE
                        def perfectoArtifact=envVariableMap.PERFECTO_ARTIFACT_VERSION %>
                        <% if (allureReportAction) { %>
                            <h2>Allure Results</h2>
                            <table>
                                <tbody>
                                    <tr>
                                        <th>Total Allure tests run:</th>
                                        <td><span
                                                class="<%=allureReportAction.getTotalCount() > 0 ? 'total' : ''%>">${allureReportAction.getTotalCount()}</span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>Failed:</th>
                                        <td><span
                                                class="<%=allureReportAction.getFailedCount() > 0 ? 'failed' : ''%>">${allureReportAction.getFailedCount()}</span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>Passed:</th>
                                        <td><span
                                                class="<%=allureReportAction.getPassedCount() > 0 ? 'passed' : ''%>">${allureReportAction.getPassedCount()}</span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>Skipped:</th>
                                        <td><span
                                                class="<%=allureReportAction.getSkipCount() > 0 ? 'skipped' : ''%>">${allureReportAction.getSkipCount()}</span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>Broken:</th>
                                        <td><span
                                                class="<%=allureReportAction.getBrokenCount() > 0 ? 'broken' : ''%>">${allureReportAction.getBrokenCount()}</span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>Success rate: </th>
                                        <td>${allureLastBuildSuccessRate}
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>Alure report: </th>
                                        <td>
                                            <a href="${rooturl}${build.url}allure/">${rooturl}${build.url}allure/
                                            </a>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>Execution History graph: </th>
                                        <td>
                                            <a href="${rooturl}${build.url}allure/graph">${rooturl}${build.url}allure/graph
                                            </a>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                            <!-- Allure results -->
                            <h2>Perfecto Results</h2>
                            <table>
                                <tbody>
                                    <tr>
                                        <th>Artifact version:</th>
                                        <td>${perfectoArtifact ? perfectoArtifact : 'Perfecto artifact version is not available'}</td>
                                    </tr>
                                    <tr>
                                        <th>Perfecto report link: </th>
                                        <td>
                                            <a href="${perfectoURL}">
                                            ${perfectoURL ? perfectoURL : 'Perfecto report is not available'}
                                            </a>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                            <% } %>
                            <!-- Perfecto results -->
</body>