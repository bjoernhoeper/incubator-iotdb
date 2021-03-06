/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.iotdb.db.qp.executor;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.iotdb.db.exception.FileNodeManagerException;
import org.apache.iotdb.db.exception.PathErrorException;
import org.apache.iotdb.db.exception.ProcessorException;
import org.apache.iotdb.db.qp.physical.PhysicalPlan;
import org.apache.iotdb.db.qp.physical.crud.QueryPlan;
import org.apache.iotdb.db.query.context.QueryContext;
import org.apache.iotdb.db.query.fill.IFill;
import org.apache.iotdb.tsfile.exception.filter.QueryFilterOptimizationException;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.read.common.Path;
import org.apache.iotdb.tsfile.read.expression.IExpression;
import org.apache.iotdb.tsfile.read.query.dataset.QueryDataSet;
import org.apache.iotdb.tsfile.utils.Pair;

public interface IQueryProcessExecutor {

  /**
   * Process Non-Query Physical plan, including insert/update/delete operation of
   * data/metadata/Privilege
   *
   * @param plan Physical Non-Query Plan
   */
  boolean processNonQuery(PhysicalPlan plan) throws ProcessorException;

  /**
   * process query plan of qp layer, construct queryDataSet.
   *
   * @param queryPlan QueryPlan
   * @return QueryDataSet
   */
  QueryDataSet processQuery(QueryPlan queryPlan, QueryContext context)
      throws IOException, FileNodeManagerException, PathErrorException,
      QueryFilterOptimizationException, ProcessorException;

  /**
   * process aggregate plan of qp layer, construct queryDataSet.
   */
  QueryDataSet aggregate(List<Path> paths, List<String> aggres, IExpression expression,
      QueryContext context)
      throws ProcessorException, IOException, PathErrorException, FileNodeManagerException, QueryFilterOptimizationException;

  /**
   * process group by plan of qp layer, construct queryDataSet.
   */
  QueryDataSet groupBy(List<Path> paths, List<String> aggres, IExpression expression,
      long unit, long origin, List<Pair<Long, Long>> intervals, QueryContext context)
      throws ProcessorException, IOException, PathErrorException, FileNodeManagerException, QueryFilterOptimizationException;

  /**
   * process fill plan of qp layer, construct queryDataSet.
   */
  QueryDataSet fill(List<Path> fillPaths, long queryTime, Map<TSDataType, IFill> fillTypes,
      QueryContext context)
      throws ProcessorException, IOException, PathErrorException, FileNodeManagerException;

  /**
   * execute update command and return whether the operator is successful.
   *
   * @param path : update series seriesPath
   * @param startTime start time in update command
   * @param endTime end time in update command
   * @param value - in type of string
   * @return - whether the operator is successful.
   */
  boolean update(Path path, long startTime, long endTime, String value)
      throws ProcessorException;

  /**
   * execute delete command and return whether the operator is successful.
   *
   * @param paths : delete series paths
   * @param deleteTime end time in delete command
   * @return - whether the operator is successful.
   */
  boolean delete(List<Path> paths, long deleteTime) throws ProcessorException;

  /**
   * execute delete command and return whether the operator is successful.
   *
   * @param path : delete series seriesPath
   * @param deleteTime end time in delete command
   * @return - whether the operator is successful.
   */
  boolean delete(Path path, long deleteTime) throws ProcessorException;

  /**
   * insert a single value. Only used in test
   *
   * @param path seriesPath to be inserted
   * @param insertTime - it's time point but not a range
   * @param value value to be inserted
   * @return - Operate Type.
   */
  int insert(Path path, long insertTime, String value) throws ProcessorException;

  /**
   * execute insert command and return whether the operator is successful.
   *
   * @param deviceId deviceId to be inserted
   * @param insertTime - it's time point but not a range
   * @param measurementList measurements to be inserted
   * @param insertValues values to be inserted
   * @return - Operate Type.
   */
  int multiInsert(String deviceId, long insertTime, String[] measurementList,
      String[] insertValues) throws ProcessorException;

  boolean judgePathExists(Path fullPath);

  /**
   * Get data type of series
   */
  TSDataType getSeriesType(Path path) throws PathErrorException;

  /**
   * Get all paths of a full path
   */
  List<String> getAllPaths(String originPath) throws PathErrorException;

  int getFetchSize();

  void setFetchSize(int fetchSize);

}
