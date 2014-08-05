;;; export-post --- Summary
;;; Commentary:
;;; Code:
(progn
  (load "~/.emacs.d/custom.el")
  (package-initialize)
  (require 'cl)
  (add-to-list 'custom-theme-load-path "~/.emacs.d/themes")
  (load-theme 'zenburn t)
  (require 'org)
  (find-file (car (last command-line-args)))
  (setq org-confirm-babel-evaluate nil)
  (org-export-as-html 3 nil "html" t nil)
  (princ (buffer-string)))
;;; export-post.el ends here
