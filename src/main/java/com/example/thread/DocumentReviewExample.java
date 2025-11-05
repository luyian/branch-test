package com.example.thread;

import java.util.*;

public class DocumentReviewExample {
    public static void main(String[] args) {
        // 创建单据列表
        List<String> documents = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            documents.add("Alice " + i);
        }
        for (int i = 1; i <= 6; i++) {
            documents.add("Bob " + i);
        }
        for (int i = 1; i <= 4; i++) {
            documents.add("Charlie " + i);
        }

        // 创建审核人员列表
        List<String> reviewers = new ArrayList<>();
        reviewers.add("Alice");
        reviewers.add("Bob");
        reviewers.add("Charlie");
        reviewers.add("David");
        reviewers.add("Eve");

        // 分配单据给审核人员
        Map<String, List<String>> assignedDocuments = assignDocuments(documents, reviewers);

        // 打印结果
        for (Map.Entry<String, List<String>> entry : assignedDocuments.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    /**
     * 将单据分配给审核人员，确保每个人不能审核自己的单据。
     *
     * @param documents 单据列表
     * @param reviewers 审核人员列表
     * @return 分配结果的 Map，键为审核人员，值为分配的单据列表
     */
    public static Map<String, List<String>> assignDocuments(List<String> documents, List<String> reviewers) {
        Map<String, List<String>> assignedDocuments = new HashMap<>();

        // 初始化每个审核人员的单据列表
        for (String reviewer : reviewers) {
            assignedDocuments.put(reviewer, new ArrayList<>());
        }

        // 打乱单据列表
        Collections.shuffle(documents);

        // 最大值数量
        int maxCount = documents.size() / reviewers.size() + 1;

        // 分配单据
        int reviewerIndex = 0;
        for (String document : documents) {
            String currentReviewer = reviewers.get(reviewerIndex % reviewers.size());
            String nextReviewer = reviewers.get((reviewerIndex + 1) % reviewers.size());

            String addReviewer = currentReviewer;
            // 确保当前单据不分配给当前审核人员
            if (document.startsWith(currentReviewer)) {
                addReviewer = nextReviewer;
            }
            assignedDocuments.get(addReviewer).add(document);
//            if (assignedDocuments.get(addReviewer).size() == maxCount) {
//                reviewers.remove(addReviewer);
//            }

            reviewerIndex++;
        }

        return assignedDocuments;
    }

}
