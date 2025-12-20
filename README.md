# ML4J - Machine Learning Library in Java

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/yourusername/ML4J/actions)

**ML4J** is a lightweight, pure Java machine learning library designed for educational purposes and simple ML tasks. It provides a robust DataFrame implementation for data manipulation and a collection of standard machine learning algorithms for classification and regression.

## Table of Contents

- [About](#about)
- [Features](#features)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
- [Usage](#usage)
    - [Linear Regression](#linear-regression)
    - [Logistic Regression](#logistic-regression)
    - [K-Nearest Neighbours](#k-nearest-neighbours)
- [Documentation](#documentation)
- [Contributing](#contributing)
- [License](#license)

## About

ML4J aims to provide a simple yet powerful interface for performing machine learning tasks in Java without the overhead of heavy external dependencies. It is built to be easily understood, making it an excellent tool for learning the internal workings of ML algorithms.

## Features

-   **DataFrame**: A flexible structure for handling tabular data, supporting CSV reading, column manipulation, and splitting.
-   **Preprocessing**:
    -   `OutlierHandler`: Remove outliers from your data.
    -   `Scaler`: Scale features using Robust Scaler.
    -   `Splitter`: Train/Test split functionality.
-   **Models**:
    -   **Regression**: Linear Regression, Logistic Regression, K-Nearest Neighbours Regression.
    -   **Classification**: K-Nearest Neighbours Classification, Support Vector Machine (SVM), Decision Tree.

## Getting Started

### Prerequisites

-   Java Development Kit (JDK) 17 or higher.
-   Apache Maven 3.6.0 or higher.

### Installation

#### Local Installation

Since ML4J is not yet hosted on Maven Central, you need to install it to your local Maven repository:

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/yourusername/ML4J.git
    cd ML4J
    ```
2.  **Install with Maven**:
    ```bash
    mvn clean install
    ```

#### Maven Dependency

Once installed locally, add the following dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>com.ml4j</groupId>
    <artifactId>ml4j</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

### Linear Regression
Predict a continuous value based on features.

```java
import mlcore.dataframe.DataFrame;
import mlcore.dataframe.transformations.Scaler;
import mlcore.dataframe.transformations.Splitter;
import mlcore.models.LinearRegression;
import java.util.ArrayList;

public class LinearRegressionExample {
    public static void main(String[] args) {
        // Load Dataset
        DataFrame df = DataFrame.readCSV("housing_data.csv", ",");

        // Prepare Data
        DataFrame X = df.dropColumn("price");
        DataFrame y = df.getColumn("price");

        // Scale Features
        Scaler scaler = new Scaler();
        for(String col : X.getData().keySet()) {
             try { X = scaler.RobustScaler(X, col); } catch (Exception e) {}
        }

        // Split Data
        Splitter splitter = new Splitter();
        var split = splitter.trainTestSplit(X, y, 0.8, 42);

        // Train
        LinearRegression model = new LinearRegression(0.01, 1000); // learning_rate, epochs
        model.train(split.get("X_train"), split.get("y_train"));

        // Predict
        DataFrame predictions = model.predict(split.get("X_test"));
        predictions.display();
    }
}
```

### Logistic Regression
Predict probabilities for a binary target (e.g., 0 or 1).

```java
import mlcore.dataframe.DataFrame;
import mlcore.models.LogisticRegression;

public class LogisticRegressionExample {
    public static void main(String[] args) {
        DataFrame df = DataFrame.readCSV("heart_disease.csv", ",");
        
        DataFrame X = df.dropColumn("target"); // target must have exactly 2 classes
        DataFrame y = df.getColumn("target");

        LogisticRegression model = new LogisticRegression(0.1, 500);
        model.train(X, y);

        DataFrame probs = model.predict(X); // Returns probabilities
        System.out.println("Probabilities:");
        probs.display();
    }
}
```

### K-Nearest Neighbours
Classify data points into multiple categories.

```java
import mlcore.dataframe.DataFrame;
import mlcore.models.KNeighboursClassification;

public class KNNExample {
    public static void main(String[] args) {
        DataFrame df = DataFrame.readCSV("iris.csv", ",");
        
        DataFrame X = df.dropColumn("species");
        DataFrame y = df.getColumn("species");

        // Initialize KNN with k=3
        KNeighboursClassification model = new KNeighboursClassification(3);
        
        model.train(X, y);

        DataFrame predictions = model.predict(X);
        predictions.display();
    }
}
```

## Documentation

For more detailed information on deployment, please refer to the [Maven Deployment Guide](MAVEN_DEPLOY.md).

## Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details on how to submit pull requests, report issues, and contribute to the project.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
