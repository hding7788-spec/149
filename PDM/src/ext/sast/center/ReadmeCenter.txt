在wt.properties里后面添加：
1.AVIDM_HOME=$(wt.home)$(dir.sep)codebase$(dir.sep)ext$(dir.sep)sast$(dir.sep)center;
2.wt.services.service.200001=ext.sast.center.service.CustomService/ext.sast.center.service.StartMQService
