"use client";

import {
  type FormEvent,
  useCallback,
  useEffect,
  useState,
} from "react";

import { useRouter } from "next/navigation";

import {
  BookOpen,
  ChevronLeft,
  ChevronRight,
  Filter,
  Pencil,
  Plus,
  Search,
  Trash2,
  X,
} from "lucide-react";

import AuthGuard from "@/components/AuthGuard";
import Header from "@/components/Header";
import Sidebar from "@/components/Sidebar";

import {
  createQuestion,
  deleteQuestion,
  getQuestions,
  updateQuestion,
} from "@/services/questionService";

import { startPracticeSession } from "@/services/practiceService";

import type {
  DifficultyLevel,
  PageResponse,
  Question,
  QuestionType,
} from "@/types/question";

const difficultyOptions: {
  label: string;
  value: DifficultyLevel | "";
}[] = [
  { label: "All difficulties", value: "" },
  { label: "Easy", value: "EASY" },
  { label: "Medium", value: "MEDIUM" },
  { label: "Hard", value: "HARD" },
];

const typeOptions: {
  label: string;
  value: QuestionType | "";
}[] = [
  { label: "All types", value: "" },
  { label: "Technical", value: "TECHNICAL" },
  { label: "Coding", value: "CODING" },
  { label: "System Design", value: "SYSTEM_DESIGN" },
  { label: "Behavioral", value: "BEHAVIORAL" },
  { label: "MCQ", value: "MCQ" },
  { label: "Other", value: "OTHER" },
];

type QuestionFormState = {
  questionText: string;
  answer: string;
  category: string;
  topic: string;
  questionType: QuestionType;
  difficultyLevel: DifficultyLevel;
  tags: string;
};

const emptyQuestionForm: QuestionFormState = {
  questionText: "",
  answer: "",
  category: "",
  topic: "",
  questionType: "TECHNICAL",
  difficultyLevel: "MEDIUM",
  tags: "",
};

export default function SavedQuestionsPage() {
  const router = useRouter();

  const [questions, setQuestions] =
    useState<PageResponse<Question> | null>(null);

  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState<10 | 20>(10);

  /*
   * Draft filter values.
   *
   * These are what the user is currently typing/selecting.
   * We do NOT call the backend on every keystroke.
   */
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("");
  const [topic, setTopic] = useState("");

  const [difficultyLevel, setDifficultyLevel] =
    useState<DifficultyLevel | "">("");

  const [questionType, setQuestionType] =
    useState<QuestionType | "">("");

  /*
   * Applied filter values.
   *
   * These are sent to the backend only after Search/Apply is clicked.
   */
  const [appliedSearch, setAppliedSearch] = useState("");
  const [appliedCategory, setAppliedCategory] = useState("");
  const [appliedTopic, setAppliedTopic] = useState("");

  const [appliedDifficultyLevel, setAppliedDifficultyLevel] =
    useState<DifficultyLevel | "">("");

  const [appliedQuestionType, setAppliedQuestionType] =
    useState<QuestionType | "">("");

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [showQuestionModal, setShowQuestionModal] =
    useState(false);

  const [editingQuestionId, setEditingQuestionId] =
    useState<string | null>(null);

  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState("");

  const [questionForm, setQuestionForm] =
    useState<QuestionFormState>(emptyQuestionForm);

  const [questionToDelete, setQuestionToDelete] =
    useState<Question | null>(null);

  const [deleting, setDeleting] = useState(false);
  const [deleteError, setDeleteError] = useState("");

  const [practicingQuestionId, setPracticingQuestionId] =
    useState<string | null>(null);

  const [practiceError, setPracticeError] = useState("");

  const loadQuestions = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const response = await getQuestions({
        page,
        size: pageSize,
        search: appliedSearch.trim() || undefined,
        category: appliedCategory.trim() || undefined,
        topic: appliedTopic.trim() || undefined,
        difficultyLevel:
          appliedDifficultyLevel || undefined,
        questionType:
          appliedQuestionType || undefined,
      });

      setQuestions(response);
    } catch (error) {
      setError(
        error instanceof Error
          ? error.message
          : "Unable to load your saved questions.",
      );
    } finally {
      setLoading(false);
    }
  }, [
    page,
    pageSize,
    appliedSearch,
    appliedCategory,
    appliedTopic,
    appliedDifficultyLevel,
    appliedQuestionType,
  ]);

  useEffect(() => {
    const timeoutId = window.setTimeout(() => {
      void loadQuestions();
    }, 0);

    return () => {
      window.clearTimeout(timeoutId);
    };
  }, [loadQuestions]);

  function handleFilterSubmit(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    setAppliedSearch(search.trim());
    setAppliedCategory(category.trim());
    setAppliedTopic(topic.trim());
    setAppliedDifficultyLevel(difficultyLevel);
    setAppliedQuestionType(questionType);

    setPage(0);
  }

  function clearFilters() {
    setSearch("");
    setCategory("");
    setTopic("");
    setDifficultyLevel("");
    setQuestionType("");

    setAppliedSearch("");
    setAppliedCategory("");
    setAppliedTopic("");
    setAppliedDifficultyLevel("");
    setAppliedQuestionType("");

    setPage(0);
  }

  function resetQuestionForm() {
    setQuestionForm(emptyQuestionForm);
    setEditingQuestionId(null);
    setFormError("");
  }

  function openAddModal() {
    resetQuestionForm();
    setShowQuestionModal(true);
  }

  function openEditModal(question: Question) {
    setFormError("");
    setEditingQuestionId(question.id);

    setQuestionForm({
      questionText: question.questionText,
      answer: question.answer ?? "",
      category: question.category,
      topic: question.topic,
      questionType: question.questionType,
      difficultyLevel: question.difficultyLevel,
      tags: question.tags ?? "",
    });

    setShowQuestionModal(true);
  }

  function closeQuestionModal() {
    if (saving) return;

    setShowQuestionModal(false);
    resetQuestionForm();
  }

  async function handleSaveQuestion(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    setFormError("");

    if (
      !questionForm.questionText.trim() ||
      !questionForm.category.trim() ||
      !questionForm.topic.trim()
    ) {
      setFormError(
        "Question, category and topic are required.",
      );
      return;
    }

    setSaving(true);

    const request = {
      questionText: questionForm.questionText.trim(),
      answer: questionForm.answer.trim() || undefined,
      category: questionForm.category.trim(),
      topic: questionForm.topic.trim(),
      questionType: questionForm.questionType,
      difficultyLevel: questionForm.difficultyLevel,
      tags: questionForm.tags.trim() || undefined,
    };

    try {
      const wasEditing = editingQuestionId !== null;

      if (editingQuestionId) {
        await updateQuestion(editingQuestionId, request);
      } else {
        await createQuestion(request);
      }

      setShowQuestionModal(false);
      resetQuestionForm();

      /*
       * Questions are sorted by permanent question number.
       * A newly created question receives the next number, so it
       * belongs at the end rather than forcing the user to page 1.
       *
       * Reload the current page for now. If the current page is no
       * longer valid after a delete, delete handling corrects it.
       */
      if (wasEditing) {
        await loadQuestions();
      } else {
        await loadQuestions();
      }
    } catch (error) {
      setFormError(
        error instanceof Error
          ? error.message
          : editingQuestionId
            ? "Unable to update the question."
            : "Unable to save the question.",
      );
    } finally {
      setSaving(false);
    }
  }

  function openDeleteModal(question: Question) {
    setDeleteError("");
    setQuestionToDelete(question);
  }

  function closeDeleteModal() {
    if (deleting) return;

    setQuestionToDelete(null);
    setDeleteError("");
  }

  async function handleDeleteQuestion() {
    if (!questionToDelete) return;

    setDeleting(true);
    setDeleteError("");

    try {
      await deleteQuestion(questionToDelete.id);

      const wasOnlyQuestionOnPage =
        questions?.content.length === 1 && page > 0;

      setQuestionToDelete(null);

      if (wasOnlyQuestionOnPage) {
        setPage((current) => Math.max(0, current - 1));
      } else {
        await loadQuestions();
      }
    } catch (error) {
      setDeleteError(
        error instanceof Error
          ? error.message
          : "Unable to delete the question.",
      );
    } finally {
      setDeleting(false);
    }
  }

  async function handlePracticeQuestion(question: Question) {
    if (practicingQuestionId) return;

    setPracticeError("");
    setPracticingQuestionId(question.id);

    try {
      const session = await startPracticeSession({
        category: question.category,
        topic: question.topic,
        difficultyLevel: question.difficultyLevel,
        questionType: question.questionType,
        totalQuestions: 1,
        questionSource: "SAVED",
        questionId: question.id,
      });

      if (session.assignedQuestions !== 1) {
        throw new Error(
          "Unable to start practice for this question.",
        );
      }

      router.push(`/practice/${session.id}`);
    } catch (error) {
      setPracticeError(
        error instanceof Error
          ? error.message
          : "Unable to start practice. Please try again.",
      );

      setPracticingQuestionId(null);
    }
  }

  function getVisiblePageNumbers() {
    if (!questions || questions.totalPages <= 1) {
      return [];
    }

    const totalPages = questions.totalPages;
    const currentPage = questions.page;

    let start = Math.max(0, currentPage - 2);
    const end = Math.min(totalPages - 1, start + 4);

    if (end - start < 4) {
      start = Math.max(0, end - 4);
    }

    const pages: number[] = [];

    for (let i = start; i <= end; i += 1) {
      pages.push(i);
    }

    return pages;
  }

  return (
    <AuthGuard>
      <div className="min-h-screen overflow-x-hidden bg-slate-50">
        <Sidebar />

        <div className="min-h-screen w-full lg:ml-64 lg:w-[calc(100%-16rem)]">
          <Header />

          <main className="w-full px-4 py-6 sm:px-6 sm:py-8 lg:px-8">
            {/* PAGE HEADER */}
            <section className="mb-6 flex flex-col gap-5 sm:mb-8 md:flex-row md:items-center md:justify-between">
              <div>
                <p className="mb-2 text-sm font-medium text-indigo-600">
                  Your question bank
                </p>

                <h1 className="text-2xl font-bold tracking-tight text-slate-900 sm:text-3xl">
                  Saved Questions
                </h1>

                <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-500">
                  Save interview questions you want to revisit,
                  organize them by topic, and practice them whenever
                  you&apos;re ready.
                </p>
              </div>

              <button
                type="button"
                onClick={openAddModal}
                className="inline-flex w-full items-center justify-center gap-2 rounded-xl bg-indigo-600 px-5 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-indigo-700 sm:w-auto"
              >
                <Plus size={18} />
                Add Question
              </button>
            </section>

            {/* SEARCH + FILTERS */}
            <section className="mb-6 rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:p-5">
              <div className="mb-4 flex items-center gap-2">
                <Filter
                  size={17}
                  className="text-indigo-600"
                />

                <h2 className="text-sm font-semibold text-slate-900">
                  Search & filter questions
                </h2>
              </div>

              <form
                onSubmit={handleFilterSubmit}
                className="space-y-3"
              >
                <div className="relative">
                  <Search
                    size={17}
                    className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400"
                  />

                  <input
                    type="text"
                    value={search}
                    onChange={(event) =>
                      setSearch(event.target.value)
                    }
                    placeholder="Search question, category, topic, tag or #number..."
                    className="w-full rounded-xl border border-slate-200 bg-slate-50 py-3 pl-10 pr-4 text-sm outline-none transition focus:border-indigo-400 focus:ring-4 focus:ring-indigo-100"
                  />
                </div>

                <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 xl:grid-cols-[1fr_1fr_0.8fr_0.9fr_auto]">
                  <input
                    type="text"
                    value={category}
                    onChange={(event) =>
                      setCategory(event.target.value)
                    }
                    placeholder="Category"
                    className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-indigo-400 focus:ring-4 focus:ring-indigo-100"
                  />

                  <input
                    type="text"
                    value={topic}
                    onChange={(event) =>
                      setTopic(event.target.value)
                    }
                    placeholder="Topic"
                    className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-indigo-400 focus:ring-4 focus:ring-indigo-100"
                  />

                  <select
                    value={difficultyLevel}
                    onChange={(event) =>
                      setDifficultyLevel(
                        event.target.value as
                          | DifficultyLevel
                          | "",
                      )
                    }
                    className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 outline-none transition focus:border-indigo-400 focus:ring-4 focus:ring-indigo-100"
                  >
                    {difficultyOptions.map((option) => (
                      <option
                        key={option.label}
                        value={option.value}
                      >
                        {option.label}
                      </option>
                    ))}
                  </select>

                  <select
                    value={questionType}
                    onChange={(event) =>
                      setQuestionType(
                        event.target.value as
                          | QuestionType
                          | "",
                      )
                    }
                    className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 outline-none transition focus:border-indigo-400 focus:ring-4 focus:ring-indigo-100"
                  >
                    {typeOptions.map((option) => (
                      <option
                        key={option.label}
                        value={option.value}
                      >
                        {option.label}
                      </option>
                    ))}
                  </select>

                  <div className="flex gap-2 sm:col-span-2 xl:col-span-1">
                    <button
                      type="submit"
                      className="flex-1 rounded-xl bg-slate-900 px-4 py-3 text-sm font-semibold text-white transition hover:bg-slate-800 xl:flex-none"
                    >
                      Search
                    </button>

                    <button
                      type="button"
                      onClick={clearFilters}
                      className="flex-1 rounded-xl border border-slate-200 px-4 py-3 text-sm font-semibold text-slate-600 transition hover:bg-slate-50 xl:flex-none"
                    >
                      Clear
                    </button>
                  </div>
                </div>
              </form>
            </section>

            {practiceError && (
              <div className="mb-6 rounded-xl border border-red-100 bg-red-50 px-4 py-3 text-sm text-red-600">
                {practiceError}
              </div>
            )}

            {/* QUESTIONS */}
            <section className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
              <div className="flex flex-col gap-4 border-b border-slate-100 px-4 py-5 sm:flex-row sm:items-center sm:justify-between sm:px-6">
                <div>
                  <h2 className="font-semibold text-slate-900">
                    Your questions
                  </h2>

                  <p className="mt-1 text-xs text-slate-500">
                    {questions
                      ? `${questions.totalElements} saved question${
                          questions.totalElements === 1 ? "" : "s"
                        }`
                      : "Loading questions..."}
                  </p>
                </div>

                <div className="flex items-center gap-4">
                  <label className="flex items-center gap-2 text-xs font-medium text-slate-500">
                    Show

                    <select
                      value={pageSize}
                      onChange={(event) => {
                        setPageSize(
                          Number(event.target.value) as 10 | 20,
                        );
                        setPage(0);
                      }}
                      className="rounded-lg border border-slate-200 bg-white px-2 py-1.5 text-xs font-semibold text-slate-700 outline-none transition focus:border-indigo-400"
                    >
                      <option value={10}>10</option>
                      <option value={20}>20</option>
                    </select>

                    per page
                  </label>

                  <BookOpen
                    size={20}
                    className="text-indigo-500"
                  />
                </div>
              </div>

              {loading ? (
                <div className="px-4 py-16 text-center sm:px-6">
                  <div className="mx-auto h-8 w-8 animate-spin rounded-full border-2 border-slate-200 border-t-indigo-600" />

                  <p className="mt-4 text-sm text-slate-500">
                    Loading your saved questions...
                  </p>
                </div>
              ) : error ? (
                <div className="px-4 py-16 text-center sm:px-6">
                  <p className="text-sm font-medium text-red-600">
                    {error}
                  </p>

                  <button
                    type="button"
                    onClick={() => void loadQuestions()}
                    className="mt-4 rounded-lg bg-slate-900 px-4 py-2 text-sm font-semibold text-white"
                  >
                    Try again
                  </button>
                </div>
              ) : !questions ||
                questions.content.length === 0 ? (
                <div className="px-4 py-16 text-center sm:px-6">
                  <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-xl bg-indigo-50 text-indigo-600">
                    <BookOpen size={22} />
                  </div>

                  <h3 className="mt-4 font-semibold text-slate-900">
                    No saved questions found
                  </h3>

                  <p className="mx-auto mt-2 max-w-md text-sm leading-6 text-slate-500">
                    No questions match your current search or
                    filters. Clear the filters or add a new
                    interview question.
                  </p>

                  <div className="mt-5 flex flex-wrap justify-center gap-2">
                    <button
                      type="button"
                      onClick={clearFilters}
                      className="rounded-xl border border-slate-200 px-4 py-2.5 text-sm font-semibold text-slate-600 transition hover:bg-slate-50"
                    >
                      Clear filters
                    </button>

                    <button
                      type="button"
                      onClick={openAddModal}
                      className="inline-flex items-center gap-2 rounded-xl bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-indigo-700"
                    >
                      <Plus size={17} />
                      Add Question
                    </button>
                  </div>
                </div>
              ) : (
                <>
                  <div className="divide-y divide-slate-100">
                    {questions.content.map((question) => (
                      <article
                        key={question.id}
                        className="px-4 py-5 transition hover:bg-slate-50/70 sm:px-6"
                      >
                        <div className="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
                          <div className="min-w-0 flex-1">
                            <div className="mb-3 flex flex-wrap gap-2">
                              {/* Permanent user-facing question number */}
                              <span className="rounded-full bg-violet-100 px-2.5 py-1 text-xs font-bold text-violet-700">
                                #{question.questionNumber}
                              </span>

                              <span className="max-w-full break-words rounded-full bg-indigo-50 px-2.5 py-1 text-xs font-semibold text-indigo-600">
                                {question.category}
                              </span>

                              <span className="max-w-full break-words rounded-full bg-slate-100 px-2.5 py-1 text-xs font-medium text-slate-600">
                                {question.topic}
                              </span>

                              <span className="rounded-full bg-amber-50 px-2.5 py-1 text-xs font-medium text-amber-700">
                                {question.difficultyLevel}
                              </span>

                              <span className="rounded-full bg-emerald-50 px-2.5 py-1 text-xs font-medium text-emerald-700">
                                {question.questionType.replaceAll(
                                  "_",
                                  " ",
                                )}
                              </span>
                            </div>

                            <h3 className="break-words text-[15px] font-semibold leading-6 text-slate-900">
                              {question.questionText}
                            </h3>

                            {question.tags && (
                              <p className="mt-3 break-words text-xs text-slate-400">
                                Tags: {question.tags}
                              </p>
                            )}
                          </div>

                          <div className="grid w-full grid-cols-3 gap-2 lg:flex lg:w-auto lg:shrink-0">
                            <button
                              type="button"
                              onClick={() =>
                                openEditModal(question)
                              }
                              className="inline-flex min-w-0 items-center justify-center gap-1.5 rounded-lg border border-slate-200 bg-white px-2 py-2.5 text-xs font-semibold text-slate-600 transition hover:border-indigo-200 hover:bg-indigo-50 hover:text-indigo-600 sm:px-3.5"
                            >
                              <Pencil
                                size={14}
                                className="shrink-0"
                              />
                              <span>Edit</span>
                            </button>

                            <button
                              type="button"
                              onClick={() =>
                                openDeleteModal(question)
                              }
                              className="inline-flex min-w-0 items-center justify-center gap-1.5 rounded-lg border border-red-100 bg-white px-2 py-2.5 text-xs font-semibold text-red-600 transition hover:bg-red-50 sm:px-3.5"
                            >
                              <Trash2
                                size={14}
                                className="shrink-0"
                              />
                              <span>Delete</span>
                            </button>

                            <button
                              type="button"
                              disabled={
                                practicingQuestionId !== null
                              }
                              onClick={() =>
                                handlePracticeQuestion(question)
                              }
                              className="min-w-0 rounded-lg border border-indigo-100 bg-indigo-50 px-2 py-2.5 text-xs font-semibold text-indigo-600 transition hover:bg-indigo-100 disabled:cursor-not-allowed disabled:opacity-60 sm:px-4"
                            >
                              {practicingQuestionId ===
                              question.id
                                ? "Starting..."
                                : "Practice"}
                            </button>
                          </div>
                        </div>
                      </article>
                    ))}
                  </div>

                  {/* PAGINATION */}
                  <div className="flex flex-col gap-4 border-t border-slate-100 px-4 py-4 sm:px-6 lg:flex-row lg:items-center lg:justify-between">
                    <div className="text-xs text-slate-500">
                      Page {questions.page + 1} of{" "}
                      {Math.max(questions.totalPages, 1)}
                      {" • "}
                      {questions.totalElements} total
                    </div>

                    {questions.totalPages > 1 && (
                      <div className="flex flex-wrap items-center gap-2">
                        <button
                          type="button"
                          disabled={questions.first}
                          onClick={() =>
                            setPage((current) =>
                              Math.max(0, current - 1),
                            )
                          }
                          className="flex items-center justify-center gap-1 rounded-lg border border-slate-200 px-3 py-2 text-xs font-semibold text-slate-600 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
                        >
                          <ChevronLeft size={14} />
                          Previous
                        </button>

                        {getVisiblePageNumbers().map(
                          (pageNumber) => (
                            <button
                              key={pageNumber}
                              type="button"
                              onClick={() =>
                                setPage(pageNumber)
                              }
                              className={`h-8 min-w-8 rounded-lg px-2 text-xs font-semibold transition ${
                                pageNumber === questions.page
                                  ? "bg-indigo-600 text-white"
                                  : "border border-slate-200 bg-white text-slate-600 hover:bg-slate-50"
                              }`}
                            >
                              {pageNumber + 1}
                            </button>
                          ),
                        )}

                        <button
                          type="button"
                          disabled={questions.last}
                          onClick={() =>
                            setPage((current) => current + 1)
                          }
                          className="flex items-center justify-center gap-1 rounded-lg border border-slate-200 px-3 py-2 text-xs font-semibold text-slate-600 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
                        >
                          Next
                          <ChevronRight size={14} />
                        </button>
                      </div>
                    )}
                  </div>
                </>
              )}
            </section>
          </main>
        </div>

        {/* ADD / EDIT QUESTION MODAL */}
        {showQuestionModal && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 p-3 backdrop-blur-sm sm:p-4">
            <div className="max-h-[92dvh] w-full max-w-2xl overflow-y-auto rounded-2xl bg-white shadow-2xl">
              <div className="flex items-start justify-between gap-4 border-b border-slate-100 px-4 py-5 sm:px-6">
                <div>
                  <h2 className="text-lg font-bold text-slate-900">
                    {editingQuestionId
                      ? "Edit Question"
                      : "Add Question"}
                  </h2>

                  <p className="mt-1 text-sm text-slate-500">
                    {editingQuestionId
                      ? "Update the details of your saved question."
                      : "Add a question to your personal interview question bank."}
                  </p>
                </div>

                <button
                  type="button"
                  disabled={saving}
                  onClick={closeQuestionModal}
                  aria-label="Close question form"
                  className="shrink-0 rounded-lg p-2 text-slate-400 transition hover:bg-slate-100 hover:text-slate-700 disabled:opacity-50"
                >
                  <X size={20} />
                </button>
              </div>

              <form
                onSubmit={handleSaveQuestion}
                className="space-y-5 p-4 sm:p-6"
              >
                <div>
                  <label className="mb-2 block text-sm font-semibold text-slate-700">
                    Question *
                  </label>

                  <textarea
                    required
                    value={questionForm.questionText}
                    onChange={(event) =>
                      setQuestionForm((current) => ({
                        ...current,
                        questionText: event.target.value,
                      }))
                    }
                    maxLength={2000}
                    rows={4}
                    placeholder="Enter the interview question..."
                    className="w-full resize-none rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-indigo-400 focus:ring-4 focus:ring-indigo-100"
                  />
                </div>

                <div className="grid gap-4 sm:grid-cols-2">
                  <div>
                    <label className="mb-2 block text-sm font-semibold text-slate-700">
                      Category *
                    </label>

                    <input
                      required
                      type="text"
                      value={questionForm.category}
                      onChange={(event) =>
                        setQuestionForm((current) => ({
                          ...current,
                          category: event.target.value,
                        }))
                      }
                      placeholder="e.g. Backend"
                      className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-indigo-400 focus:ring-4 focus:ring-indigo-100"
                    />
                  </div>

                  <div>
                    <label className="mb-2 block text-sm font-semibold text-slate-700">
                      Topic *
                    </label>

                    <input
                      required
                      type="text"
                      value={questionForm.topic}
                      onChange={(event) =>
                        setQuestionForm((current) => ({
                          ...current,
                          topic: event.target.value,
                        }))
                      }
                      placeholder="e.g. Spring Boot"
                      className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-indigo-400 focus:ring-4 focus:ring-indigo-100"
                    />
                  </div>
                </div>

                <div className="grid gap-4 sm:grid-cols-2">
                  <div>
                    <label className="mb-2 block text-sm font-semibold text-slate-700">
                      Question Type *
                    </label>

                    <select
                      value={questionForm.questionType}
                      onChange={(event) =>
                        setQuestionForm((current) => ({
                          ...current,
                          questionType:
                            event.target
                              .value as QuestionType,
                        }))
                      }
                      className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-indigo-400 focus:ring-4 focus:ring-indigo-100"
                    >
                      {typeOptions
                        .filter((option) => option.value)
                        .map((option) => (
                          <option
                            key={option.value}
                            value={option.value}
                          >
                            {option.label}
                          </option>
                        ))}
                    </select>
                  </div>

                  <div>
                    <label className="mb-2 block text-sm font-semibold text-slate-700">
                      Difficulty *
                    </label>

                    <select
                      value={
                        questionForm.difficultyLevel
                      }
                      onChange={(event) =>
                        setQuestionForm((current) => ({
                          ...current,
                          difficultyLevel:
                            event.target
                              .value as DifficultyLevel,
                        }))
                      }
                      className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-indigo-400 focus:ring-4 focus:ring-indigo-100"
                    >
                      {difficultyOptions
                        .filter((option) => option.value)
                        .map((option) => (
                          <option
                            key={option.value}
                            value={option.value}
                          >
                            {option.label}
                          </option>
                        ))}
                    </select>
                  </div>
                </div>

                <div>
                  <label className="mb-2 block text-sm font-semibold text-slate-700">
                    Reference Answer
                    <span className="ml-1 font-normal text-slate-400">
                      (optional)
                    </span>
                  </label>

                  <textarea
                    value={questionForm.answer}
                    onChange={(event) =>
                      setQuestionForm((current) => ({
                        ...current,
                        answer: event.target.value,
                      }))
                    }
                    maxLength={5000}
                    rows={4}
                    placeholder="Add your reference answer if you have one..."
                    className="w-full resize-none rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-indigo-400 focus:ring-4 focus:ring-indigo-100"
                  />
                </div>

                <div>
                  <label className="mb-2 block text-sm font-semibold text-slate-700">
                    Tags
                    <span className="ml-1 font-normal text-slate-400">
                      (optional)
                    </span>
                  </label>

                  <input
                    type="text"
                    value={questionForm.tags}
                    onChange={(event) =>
                      setQuestionForm((current) => ({
                        ...current,
                        tags: event.target.value,
                      }))
                    }
                    maxLength={1000}
                    placeholder="e.g. Spring, DI, IoC"
                    className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-indigo-400 focus:ring-4 focus:ring-indigo-100"
                  />
                </div>

                {formError && (
                  <div className="rounded-xl border border-red-100 bg-red-50 px-4 py-3 text-sm text-red-600">
                    {formError}
                  </div>
                )}

                <div className="grid grid-cols-2 gap-3 border-t border-slate-100 pt-5 sm:flex sm:justify-end">
                  <button
                    type="button"
                    disabled={saving}
                    onClick={closeQuestionModal}
                    className="rounded-xl border border-slate-200 px-4 py-3 text-sm font-semibold text-slate-600 transition hover:bg-slate-50 disabled:opacity-50 sm:px-5"
                  >
                    Cancel
                  </button>

                  <button
                    type="submit"
                    disabled={saving}
                    className="rounded-xl bg-indigo-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-60 sm:px-5"
                  >
                    {saving
                      ? editingQuestionId
                        ? "Updating..."
                        : "Saving..."
                      : editingQuestionId
                        ? "Update Question"
                        : "Save Question"}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* DELETE QUESTION MODAL */}
        {questionToDelete && (
          <div className="fixed inset-0 z-[60] flex items-center justify-center bg-slate-950/50 p-4 backdrop-blur-sm">
            <div className="w-full max-w-md rounded-2xl bg-white shadow-2xl">
              <div className="p-5 sm:p-6">
                <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-red-50 text-red-600">
                  <Trash2 size={22} />
                </div>

                <h2 className="mt-5 text-xl font-bold text-slate-900">
                  Delete Question?
                </h2>

                <p className="mt-2 text-sm leading-6 text-slate-500">
                  This question will be permanently removed from
                  your saved question bank. Its permanent question
                  number #{questionToDelete.questionNumber} will not
                  be reused.
                </p>

                <div className="mt-5 rounded-xl border border-slate-200 bg-slate-50 p-4">
                  <p className="mb-1 text-xs font-bold text-indigo-600">
                    #{questionToDelete.questionNumber}
                  </p>

                  <p className="break-words text-sm font-semibold leading-6 text-slate-800">
                    {questionToDelete.questionText}
                  </p>
                </div>

                {deleteError && (
                  <div className="mt-4 rounded-xl border border-red-100 bg-red-50 px-4 py-3 text-sm text-red-600">
                    {deleteError}
                  </div>
                )}

                <div className="mt-6 grid grid-cols-2 gap-3 sm:flex sm:justify-end">
                  <button
                    type="button"
                    disabled={deleting}
                    onClick={closeDeleteModal}
                    className="rounded-xl border border-slate-200 px-4 py-3 text-sm font-semibold text-slate-600 transition hover:bg-slate-50 disabled:opacity-50 sm:px-5"
                  >
                    Cancel
                  </button>

                  <button
                    type="button"
                    disabled={deleting}
                    onClick={handleDeleteQuestion}
                    className="inline-flex items-center justify-center gap-2 rounded-xl bg-red-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-60 sm:px-5"
                  >
                    <Trash2 size={16} />
                    {deleting ? "Deleting..." : "Delete"}
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </AuthGuard>
  );
}