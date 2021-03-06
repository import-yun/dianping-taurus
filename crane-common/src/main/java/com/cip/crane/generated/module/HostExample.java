package com.cip.crane.generated.module;

import java.util.ArrayList;
import java.util.List;

public class HostExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table TaurusHost
     *
     * @mbggenerated Thu May 15 16:53:11 HKT 2014
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table TaurusHost
     *
     * @mbggenerated Thu May 15 16:53:11 HKT 2014
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table TaurusHost
     *
     * @mbggenerated Thu May 15 16:53:11 HKT 2014
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TaurusHost
     *
     * @mbggenerated Thu May 15 16:53:11 HKT 2014
     */
    public HostExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TaurusHost
     *
     * @mbggenerated Thu May 15 16:53:11 HKT 2014
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TaurusHost
     *
     * @mbggenerated Thu May 15 16:53:11 HKT 2014
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TaurusHost
     *
     * @mbggenerated Thu May 15 16:53:11 HKT 2014
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TaurusHost
     *
     * @mbggenerated Thu May 15 16:53:11 HKT 2014
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TaurusHost
     *
     * @mbggenerated Thu May 15 16:53:11 HKT 2014
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TaurusHost
     *
     * @mbggenerated Thu May 15 16:53:11 HKT 2014
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TaurusHost
     *
     * @mbggenerated Thu May 15 16:53:11 HKT 2014
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TaurusHost
     *
     * @mbggenerated Thu May 15 16:53:11 HKT 2014
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TaurusHost
     *
     * @mbggenerated Thu May 15 16:53:11 HKT 2014
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TaurusHost
     *
     * @mbggenerated Thu May 15 16:53:11 HKT 2014
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table TaurusHost
     *
     * @mbggenerated Thu May 15 16:53:11 HKT 2014
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andNameIsNull() {
            addCriterion("name is null");
            return (Criteria) this;
        }

        public Criteria andNameIsNotNull() {
            addCriterion("name is not null");
            return (Criteria) this;
        }

        public Criteria andNameEqualTo(String value) {
            addCriterion("name =", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotEqualTo(String value) {
            addCriterion("name <>", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThan(String value) {
            addCriterion("name >", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThanOrEqualTo(String value) {
            addCriterion("name >=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThan(String value) {
            addCriterion("name <", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThanOrEqualTo(String value) {
            addCriterion("name <=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLike(String value) {
            addCriterion("name like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotLike(String value) {
            addCriterion("name not like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameIn(List<String> values) {
            addCriterion("name in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotIn(List<String> values) {
            addCriterion("name not in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameBetween(String value1, String value2) {
            addCriterion("name between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotBetween(String value1, String value2) {
            addCriterion("name not between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andIpIsNull() {
            addCriterion("ip is null");
            return (Criteria) this;
        }

        public Criteria andIpIsNotNull() {
            addCriterion("ip is not null");
            return (Criteria) this;
        }

        public Criteria andIpEqualTo(String value) {
            addCriterion("ip =", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpNotEqualTo(String value) {
            addCriterion("ip <>", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpGreaterThan(String value) {
            addCriterion("ip >", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpGreaterThanOrEqualTo(String value) {
            addCriterion("ip >=", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpLessThan(String value) {
            addCriterion("ip <", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpLessThanOrEqualTo(String value) {
            addCriterion("ip <=", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpLike(String value) {
            addCriterion("ip like", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpNotLike(String value) {
            addCriterion("ip not like", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpIn(List<String> values) {
            addCriterion("ip in", values, "ip");
            return (Criteria) this;
        }

        public Criteria andIpNotIn(List<String> values) {
            addCriterion("ip not in", values, "ip");
            return (Criteria) this;
        }

        public Criteria andIpBetween(String value1, String value2) {
            addCriterion("ip between", value1, value2, "ip");
            return (Criteria) this;
        }

        public Criteria andIpNotBetween(String value1, String value2) {
            addCriterion("ip not between", value1, value2, "ip");
            return (Criteria) this;
        }

        public Criteria andPoolidIsNull() {
            addCriterion("poolID is null");
            return (Criteria) this;
        }

        public Criteria andPoolidIsNotNull() {
            addCriterion("poolID is not null");
            return (Criteria) this;
        }

        public Criteria andPoolidEqualTo(Integer value) {
            addCriterion("poolID =", value, "poolid");
            return (Criteria) this;
        }

        public Criteria andPoolidNotEqualTo(Integer value) {
            addCriterion("poolID <>", value, "poolid");
            return (Criteria) this;
        }

        public Criteria andPoolidGreaterThan(Integer value) {
            addCriterion("poolID >", value, "poolid");
            return (Criteria) this;
        }

        public Criteria andPoolidGreaterThanOrEqualTo(Integer value) {
            addCriterion("poolID >=", value, "poolid");
            return (Criteria) this;
        }

        public Criteria andPoolidLessThan(Integer value) {
            addCriterion("poolID <", value, "poolid");
            return (Criteria) this;
        }

        public Criteria andPoolidLessThanOrEqualTo(Integer value) {
            addCriterion("poolID <=", value, "poolid");
            return (Criteria) this;
        }

        public Criteria andPoolidIn(List<Integer> values) {
            addCriterion("poolID in", values, "poolid");
            return (Criteria) this;
        }

        public Criteria andPoolidNotIn(List<Integer> values) {
            addCriterion("poolID not in", values, "poolid");
            return (Criteria) this;
        }

        public Criteria andPoolidBetween(Integer value1, Integer value2) {
            addCriterion("poolID between", value1, value2, "poolid");
            return (Criteria) this;
        }

        public Criteria andPoolidNotBetween(Integer value1, Integer value2) {
            addCriterion("poolID not between", value1, value2, "poolid");
            return (Criteria) this;
        }

        public Criteria andIsconnectedIsNull() {
            addCriterion("isConnected is null");
            return (Criteria) this;
        }

        public Criteria andIsconnectedIsNotNull() {
            addCriterion("isConnected is not null");
            return (Criteria) this;
        }

        public Criteria andIsconnectedEqualTo(Boolean value) {
            addCriterion("isConnected =", value, "isconnected");
            return (Criteria) this;
        }

        public Criteria andIsconnectedNotEqualTo(Boolean value) {
            addCriterion("isConnected <>", value, "isconnected");
            return (Criteria) this;
        }

        public Criteria andIsconnectedGreaterThan(Boolean value) {
            addCriterion("isConnected >", value, "isconnected");
            return (Criteria) this;
        }

        public Criteria andIsconnectedGreaterThanOrEqualTo(Boolean value) {
            addCriterion("isConnected >=", value, "isconnected");
            return (Criteria) this;
        }

        public Criteria andIsconnectedLessThan(Boolean value) {
            addCriterion("isConnected <", value, "isconnected");
            return (Criteria) this;
        }

        public Criteria andIsconnectedLessThanOrEqualTo(Boolean value) {
            addCriterion("isConnected <=", value, "isconnected");
            return (Criteria) this;
        }

        public Criteria andIsconnectedIn(List<Boolean> values) {
            addCriterion("isConnected in", values, "isconnected");
            return (Criteria) this;
        }

        public Criteria andIsconnectedNotIn(List<Boolean> values) {
            addCriterion("isConnected not in", values, "isconnected");
            return (Criteria) this;
        }

        public Criteria andIsconnectedBetween(Boolean value1, Boolean value2) {
            addCriterion("isConnected between", value1, value2, "isconnected");
            return (Criteria) this;
        }

        public Criteria andIsconnectedNotBetween(Boolean value1, Boolean value2) {
            addCriterion("isConnected not between", value1, value2, "isconnected");
            return (Criteria) this;
        }

        public Criteria andIsonlineIsNull() {
            addCriterion("isOnline is null");
            return (Criteria) this;
        }

        public Criteria andIsonlineIsNotNull() {
            addCriterion("isOnline is not null");
            return (Criteria) this;
        }

        public Criteria andIsonlineEqualTo(Boolean value) {
            addCriterion("isOnline =", value, "isonline");
            return (Criteria) this;
        }

        public Criteria andIsonlineNotEqualTo(Boolean value) {
            addCriterion("isOnline <>", value, "isonline");
            return (Criteria) this;
        }

        public Criteria andIsonlineGreaterThan(Boolean value) {
            addCriterion("isOnline >", value, "isonline");
            return (Criteria) this;
        }

        public Criteria andIsonlineGreaterThanOrEqualTo(Boolean value) {
            addCriterion("isOnline >=", value, "isonline");
            return (Criteria) this;
        }

        public Criteria andIsonlineLessThan(Boolean value) {
            addCriterion("isOnline <", value, "isonline");
            return (Criteria) this;
        }

        public Criteria andIsonlineLessThanOrEqualTo(Boolean value) {
            addCriterion("isOnline <=", value, "isonline");
            return (Criteria) this;
        }

        public Criteria andIsonlineIn(List<Boolean> values) {
            addCriterion("isOnline in", values, "isonline");
            return (Criteria) this;
        }

        public Criteria andIsonlineNotIn(List<Boolean> values) {
            addCriterion("isOnline not in", values, "isonline");
            return (Criteria) this;
        }

        public Criteria andIsonlineBetween(Boolean value1, Boolean value2) {
            addCriterion("isOnline between", value1, value2, "isonline");
            return (Criteria) this;
        }

        public Criteria andIsonlineNotBetween(Boolean value1, Boolean value2) {
            addCriterion("isOnline not between", value1, value2, "isonline");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table TaurusHost
     *
     * @mbggenerated do_not_delete_during_merge Thu May 15 16:53:11 HKT 2014
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table TaurusHost
     *
     * @mbggenerated Thu May 15 16:53:11 HKT 2014
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}