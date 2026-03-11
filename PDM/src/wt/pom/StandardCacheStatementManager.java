//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wt.pom;

import java.lang.ref.SoftReference;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import ext.casc.ixb.ExpImpLogger;
import ext.test.DebugLogger;
import org.apache.log4j.Logger;
import wt.log4j.LogR;

public class StandardCacheStatementManager extends BasicStatementManager {
    private static final Logger LOGGER = LogR.getLogger("wt.pom.statementCache");
    private volatile StatementCache statementCache = null;
    private volatile SoftReference<StatementCache> statementCacheReference = null;

    public StandardCacheStatementManager() {
    }

    public StandardCacheStatementManager(StandardCacheStatementManager var1) {
        this.statementCache = var1.statementCache;
        this.statementCacheReference = var1.statementCacheReference;
    }

    public void freeConnection(WTConnection var1) {
        StatementCache var2 = this.statementCache;
        if (var2 != null) {
            this.statementCacheReference = new SoftReference(var2);
            this.statementCache = null;
        }

    }

    public void clear() {
        StatementCache var1 = this.statementCache;
        if (var1 != null) {
            var1.clear();
        } else {
            SoftReference var2 = this.statementCacheReference;
            if (var2 != null) {
                var1 = (StatementCache)var2.get();
                if (var1 != null) {
                    var1.clear();
                }
            }
        }

    }

    public PreparedStatement prepare(WTConnection var1, String var2, boolean var3) throws SQLException {
        StatementCache var4 = this.getStatementCache();
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("prepare(): cache=" + var4);
        }

        Object var5 = var4.get(var2);
        DebugLogger logger =  DebugLogger.getInstance();
        logger.log("");
        logger.log("---------------------------具体SQL执行分割线---------------------------------------");
        if(var2.contains("BEGIN")){
            logger.log("存储过程");
        }
        logger.log(var2);


        if (var5 == null) {
            LOGGER.debug("prepare() adding to cache");
            var5 = this._prepare(var1, var2, var3);
            var4.put(var2, var5);
        } else if (var5 == StatementCache.BUSY) {
            var5 = this._prepare(var1, var2, var3);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("prepare(): statement=" + var5 + " sql=" + var2);
        }

        return (PreparedStatement)var5;
    }

    public void free(String var1, PreparedStatement var2, boolean var3, boolean var4) throws SQLException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("free(): statement=" + var2 + " sql=" + var1);
        }

        StatementCache var5 = this.statementCache;
        if (!var3 && var4) {
            try {
                var2.clearParameters();
                var5.free(var1, var2);
            } catch (Exception var7) {
                var5.remove(var1);
                var2.close();
                LOGGER.info("free()", var7);
            }
        } else {
            var5.remove(var1);
            var2.close();
        }

    }

    protected StatementCache getStatementCache() {
        StatementCache var1 = this.statementCache;
        if (var1 == null) {
            SoftReference var2 = this.statementCacheReference;
            if (var2 != null) {
                this.statementCache = var1 = (StatementCache)var2.get();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("getStatementCache(): accessing StatementCache from soft reference: collected=" + (var1 == null));
                }
            }

            if (var1 == null) {
                this.statementCache = var1 = new StatementCache(DBProperties.STATEMENT_CACHE_SIZE, DBProperties.CACHED_STATEMENT_REUSE_LIMIT);
            }
        }

        return var1;
    }
}
