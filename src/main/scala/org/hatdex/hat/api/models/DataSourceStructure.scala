/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.hat.api.models

case class DataSourceField(name: String, description: String, fields: Option[List[DataSourceField]])
case class DataSourceDataset(name: String, description: String, fields: List[DataSourceField])
case class DataSourceStructure(source: String, datasets: List[DataSourceDataset])
