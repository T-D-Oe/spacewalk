/**
 * Copyright (c) 2013 Red Hat, Inc.
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */

package com.redhat.rhn.frontend.xmlrpc.system.crash;

import com.redhat.rhn.domain.rhnpackage.PackageArch;
import com.redhat.rhn.domain.rhnpackage.PackageEvr;
import com.redhat.rhn.domain.rhnpackage.PackageEvrFactory;
import com.redhat.rhn.domain.rhnpackage.PackageFactory;
import com.redhat.rhn.domain.rhnpackage.PackageName;
import com.redhat.rhn.domain.server.Crash;
import com.redhat.rhn.domain.server.CrashCount;
import com.redhat.rhn.domain.server.CrashFile;
import com.redhat.rhn.domain.server.Server;
import com.redhat.rhn.domain.user.User;
import com.redhat.rhn.frontend.xmlrpc.BaseHandler;
import com.redhat.rhn.frontend.xmlrpc.NoCrashesFoundException;
import com.redhat.rhn.frontend.xmlrpc.system.XmlRpcSystemHelper;
import com.redhat.rhn.manager.system.CrashManager;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CrashHandler
 * @version $Rev$
 * @xmlrpc.namespace system.crash
 * @xmlrpc.doc Provides methods to access and modify software crash information.
 */
public class CrashHandler extends BaseHandler {

    private static Logger log = Logger.getLogger(CrashHandler.class);

    private CrashCount getCrashCount(Server serverIn) {
        CrashCount crashCount = serverIn.getCrashCount();
        if (crashCount == null) {
            throw new NoCrashesFoundException();
        }
        return crashCount;
    }

    /**
     * Return date of last software crashes report for given system.
     * @param sessionKey Session key
     * @param serverId Server ID
     * @return Date of the last software crash report.
     *
     * @xmlrpc.doc Return date of last software crashes report for given system
     * @xmlrpc.param @param("string", "sessionKey")
     * @xmlrpc.param #param("int", "serverId")
     * @xmlrpc.returntype dateTime.iso8601 - Date of the last software crash report.
     */
    public Date getLastReportDate(String sessionKey, Integer serverId) {
        User loggedInUser = getLoggedInUser(sessionKey);
        XmlRpcSystemHelper sysHelper = XmlRpcSystemHelper.getInstance();
        Server server = sysHelper.lookupServer(loggedInUser, serverId);

        CrashCount crashCount = getCrashCount(server);
        return crashCount.getLastReport();
    }


    /**
     * Return number of unique software crashes for given system.
     * @param sessionKey Session key
     * @param serverId Server ID
     * @return Number of unique software crashes.
     *
     * @xmlrpc.doc Return number of unique software recorded crashes for given system
     * @xmlrpc.param @param("string", "sessionKey")
     * @xmlrpc.param #param("int", "serverId")
     * @xmlrpc.returntype int - Number of unique software crashes
     */
    public long getUniqueCrashCount(String sessionKey, Integer serverId) {
        User loggedInUser = getLoggedInUser(sessionKey);
        XmlRpcSystemHelper sysHelper = XmlRpcSystemHelper.getInstance();
        Server server = sysHelper.lookupServer(loggedInUser, serverId);

        CrashCount crashCount = getCrashCount(server);
        return crashCount.getUniqueCrashCount();
    }

    /**
     * Return total number of software recorded crashes for given system.
     * @param sessionKey Session key
     * @param serverId Server ID
     * @return Total number of recorded software crashes.
     *
     * @xmlrpc.doc Return total number of software recorded crashes for given system
     * @xmlrpc.param @param("string", "sessionKey")
     * @xmlrpc.param #param("int", "serverId")
     * @xmlrpc.returntype int - Total number of recorded software crashes
     */
    public long getTotalCrashCount(String sessionKey, Integer serverId) {
        User loggedInUser = getLoggedInUser(sessionKey);
        XmlRpcSystemHelper sysHelper = XmlRpcSystemHelper.getInstance();
        Server server = sysHelper.lookupServer(loggedInUser, serverId);

        CrashCount crashCount = getCrashCount(server);
        return crashCount.getTotalCrashCount();
    }

    /**
     * Returns list of software crashes for a system.
     * @param sessionKey Session key
     * @param serverId Server ID
     * @return Returns list of software crashes for given system id.
     *
     * @xmlrpc.doc Return list of software crashes for a system.
     * @xmlrpc.param @param("string", "sessionKey")
     * @xmlrpc.param #param("int", "serverId")
     * @xmlrpc.returntype
     *     #array()
     *         #struct("crash")
     *             #prop("int", "id")
     *             #prop("string", "crash")
     *             #prop("string", "path")
     *             #prop("int", "count")
     *             #prop("string", "analyzer")
     *             #prop("string", "architecture")
     *             #prop("string", "cmdline")
     *             #prop("string", "component")
     *             #prop("string", "executable")
     *             #prop("string", "kernel")
     *             #prop("string", "reason")
     *             #prop("string", "username")
     *             #prop("date", "created")
     *             #prop("date", "modified")
     *         #struct_end()
     *     #array_end()
     */
    public List listSystemCrashes(String sessionKey, Integer serverId) {
        User loggedInUser = getLoggedInUser(sessionKey);
        XmlRpcSystemHelper sysHelper = XmlRpcSystemHelper.getInstance();
        Server server = sysHelper.lookupServer(loggedInUser, serverId);

        List returnList = new ArrayList();

        for (Crash crash : server.getCrashes()) {
            Map crashMap = new HashMap();
            crashMap.put("id", crash.getId());
            crashMap.put("crash", crash.getCrash());
            crashMap.put("path", crash.getPath());
            crashMap.put("count", crash.getCount());
            crashMap.put("analyzer", crash.getAnalyzer());
            crashMap.put("architecture", crash.getArchitecture());
            crashMap.put("cmdline", crash.getCmdline());
            crashMap.put("component", crash.getComponent());
            crashMap.put("executable", crash.getExecutable());
            crashMap.put("kernel", crash.getKernel());
            crashMap.put("reason", crash.getReason());
            crashMap.put("username", crash.getUsername());
            crashMap.put("created", crash.getCreated());
            crashMap.put("modified", crash.getModified());

            if (crash.getPackageNameId() != null) {
                PackageName pname = PackageFactory.lookupPackageName(
                    crash.getPackageNameId());
                crashMap.put("package_name", pname.getName());
            }

            if (crash.getPackageEvrId() != null) {
                PackageEvr pevr = PackageEvrFactory.lookupPackageEvrById(
                    crash.getPackageEvrId());
                crashMap.put("package_epoch", pevr.getEpoch());
                crashMap.put("package_version", pevr.getVersion());
                crashMap.put("package_release", pevr.getRelease());
            }

            if (crash.getPackageArchId() != null) {
                PackageArch parch = PackageFactory.lookupPackageArchById(
                    crash.getPackageArchId());
                crashMap.put("package_arch", parch.getLabel());
            }

            returnList.add(crashMap);
        }

        return returnList;
    }

    /**
     * Returns list of crash files for a given crash id.
     * @param sessionKey Session key
     * @param crashId Crash ID
     * @return Returns list of crash files.
     *
     * @xmlrpc.doc Return list of crash files for given crash id.
     * @xmlrpc.param @param("string", "sessionKey")
     * @xmlrpc.param #param("int", "crashId")
     * @xmlrpc.returntype
     *     #array()
     *         #struct("crashFile")
     *             #prop("int", "id")
     *             #prop("string", "filename")
     *             #prop("string", "path")
     *             #prop("int", "filesize")
     *             #prop("date", "created")
     *             #prop("date", "modified")
     *         #struct_end()
     *     #array_end()
     */
    public List listSystemCrashFiles(String sessionKey, Integer crashId) {
        User loggedInUser = getLoggedInUser(sessionKey);
        Crash crash = CrashManager.lookupCrashByUserAndId(loggedInUser,
                      new Long(crashId.longValue()));

        List returnList = new ArrayList();

        for (CrashFile crashFile : crash.getCrashFiles()) {
            HashMap crashMap = new HashMap();
            crashMap.put("id", crashFile.getId());
            crashMap.put("filename", crashFile.getFilename());
            crashMap.put("path", crashFile.getPath());
            crashMap.put("filesize", crashFile.getFilesize());
            crashMap.put("created", crashFile.getCreated());
            crashMap.put("modified", crashFile.getModified());
            returnList.add(crashMap);
        }

        return returnList;
    }

    /**
     * Delete a crash with given crash id.
     * @param sessionKey Session key
     * @param crashId Crash ID
     * @return 1 In case of success, exception otherwise.
     *
     * @xmlrpc.doc Delete a crash with given crash id.
     * @xmlrpc.param @param("string", "sessionKey")
     * @xmlrpc.param #param("int", "crashId")
     * @xmlrpc.returntype #return_int_success()
     */
    public Integer deleteCrash(String sessionKey, Integer crashId) {
        User loggedInUser = getLoggedInUser(sessionKey);
        CrashManager.deleteCrash(loggedInUser, new Long(crashId.longValue()));
        return 1;
    }
}