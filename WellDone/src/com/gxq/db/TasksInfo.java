/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gxq.db;

import android.provider.BaseColumns;

public final class TasksInfo {

    // This class cannot be instantiated
    private TasksInfo() {}
    
    public static final class Task implements BaseColumns {
        // This class cannot be instantiated
        private Task() {}
        
        //public columns
        //this id is the copy of the row id 
        public static final String ID = "_id";
        
        //服务端
//        Id	int	任务的序号	否		主键，数据库从1开始自增
        public static final String Server_Id = "Id";
        
        //userid
        public static final String UserId = "UserId";
        
//        UserName	char(50)	用户名	否		外键
        public static final String UserName = "UserName";
        
//        Title	char(140)	标题	否		最长140个字符
        public static final String Title = "Title";
        
//        RemindTime	datetime	提醒时间	可	空	根据此时间来区分今日任务，明日任务和以后任务
        public static final String RemindTime = "RemindTime";
        
//        Priority	int	优先级	否	2	1表示优先级低，2表示优先级中，3表示优先级高
        public static final String Priority = "Priority";
        
//        RemindPattern	int	提醒方式	否	0	0：不提醒
//        1：提醒
        public static final String RemindPattern = "RemindPattern";
        
//        IsComplete	int 	是否完成	否	0	0：未完成
//        1：完成
        public static final String IsComplete = "IsComplete";
        
//        EditTime	Datetime	编辑时间	否		待办任务的编辑时间
        public static final String EditTime = "EditTime";
        
//        1：过期
//        IsCommit	int	提交标志	可		0：不需提交，
//      1：离线状态下添加。
//      2：离线状态下修改。
//      3：离线状态下删除。 
        public static final String IsCommit = "IsCommit";
        
        //IsRemind	int	该记录是否提醒过	否	0	0：没有被提醒过
//        1：已经被提醒过
        public static final String IsRemind = "IsRemind";
        
        public static final String  DeleteFlag = "DeleteFlag";

    }
}
