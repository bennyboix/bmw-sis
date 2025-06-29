/**
 * BMW SIS Admin Portal JavaScript
 * Main functionality for admin dashboard operations
 */

$(document).ready(function() {
    console.log('Admin portal initialized successfully');
    
    // Initialize all components
    initializeModals();
    initializeSearch();
    initializeStats();
    initializeForms();
    initializeTooltips();
    
    // CSRF token setup for AJAX requests
    setupCSRF();
    
    // Initialize dashboard animations
    initializeDashboardAnimations();
});

// Initialize dashboard animations and counters
function initializeDashboardAnimations() {
    // Remove any loading indicators first
    $('.loading').remove();
    $('.stat-card .loading').remove();
    
    // Ensure stat cards are visible and loaded
    $('.stat-card').removeClass('loading').addClass('loaded').css('opacity', '1');
    
    // Counter animation for stat values
    $('.stat-value').each(function() {
        const $this = $(this);
        const countTo = parseInt($this.text()) || 0;
        
        // Ensure the value is displayed
        $this.text(countTo);
        
        // Optional: Add counting animation only if value is greater than 0
        if (countTo > 0 && countTo < 1000) {
            $this.text('0');
            $({ countNum: 0 }).animate({
                countNum: countTo
            }, {
                duration: 1500,
                easing: 'swing',
                step: function() {
                    $this.text(Math.floor(this.countNum));
                },
                complete: function() {
                    $this.text(countTo);
                }
            });
        }
    });
}

// Initialize tooltips
function initializeTooltips() {
    $('[data-tooltip]').hover(
        function() {
            const tooltip = $(this).attr('data-tooltip');
            $(this).append('<div class="tooltip-popup">' + tooltip + '</div>');
        },
        function() {
            $('.tooltip-popup').remove();
        }
    );
}

// Initialize modals
function initializeModals() {
    // Modal open/close functionality
    $('.modal-trigger').on('click', function(e) {
        e.preventDefault();
        const targetModal = $(this).data('target');
        $(targetModal).fadeIn(300);
        $('body').addClass('modal-open');
    });
    
    $('.modal .close, .modal-backdrop').on('click', function() {
    $('.modal').fadeOut(300);
    $('body').removeClass('modal-open');
    });
    
    // Prevent modal close when clicking inside modal content
    $('.modal-content').on('click', function(e) {
        e.stopPropagation();
    });
    
    // ESC key to close modal
    $(document).on('keydown', function(e) {
        if (e.keyCode === 27) { // ESC key
            $('.modal').fadeOut(300);
            $('body').removeClass('modal-open');
        }
    });
}

// Initialize search functionality
function initializeSearch() {
    $('.search-box').on('input', function() {
        const searchTerm = $(this).val().toLowerCase();
        const targetTable = $(this).data('target') || 'table';
        
        $(targetTable + ' tbody tr').each(function() {
            const rowText = $(this).text().toLowerCase();
            if (rowText.includes(searchTerm)) {
                $(this).show();
            } else {
                $(this).hide();
            }
        });
        
        // Update result count
        const visibleRows = $(targetTable + ' tbody tr:visible').length;
        $('.search-results').text(visibleRows + ' results found');
    });
}

// Initialize statistics
function initializeStats() {
    // Remove any loading states and add loaded class immediately
    $('.stat-card').removeClass('loading').addClass('loaded');
    
    // Ensure stat values are properly displayed
    $('.stat-value').each(function() {
        const $this = $(this);
        const value = $this.text().trim();
        if (value === '' || value === 'undefined' || value === 'null') {
            $this.text('0');
        }
    });
}

// Initialize forms
function initializeForms() {
    // Form validation
    $('form').on('submit', function(e) {
        const form = $(this);
        let isValid = true;
    
    // Check required fields
        form.find('[required]').each(function() {
            if (!$(this).val().trim()) {
                $(this).addClass('error');
                isValid = false;
            } else {
                $(this).removeClass('error');
            }
        });
        
        if (!isValid) {
            e.preventDefault();
            showAlert('Please fill in all required fields', 'error');
        }
    });
    
    // Clear error state on input
    $('input, select, textarea').on('input change', function() {
        $(this).removeClass('error');
    });
    
    // Auto-save functionality for forms
    $('form[data-autosave]').on('input change', debounce(function() {
        const form = $(this);
        saveFormData(form);
    }, 1000));
}

// Setup CSRF token for AJAX requests
function setupCSRF() {
    const token = $('meta[name="_csrf"]').attr('content');
    const header = $('meta[name="_csrf_header"]').attr('content');
    
    if (token && header) {
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });
    }
}

// Utility function: Debounce
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Show alert messages
function showAlert(message, type = 'info') {
    const alertClass = type === 'error' ? 'alert-error' : 'alert-success';
    const alertHtml = `
        <div class="alert ${alertClass}" style="display: none;">
            <i class="fas ${type === 'error' ? 'fa-exclamation-triangle' : 'fa-check-circle'}"></i>
            <span>${message}</span>
            <button class="alert-close" onclick="$(this).parent().fadeOut()">&times;</button>
                </div>
            `;
            
    $('.main-content').prepend(alertHtml);
    $('.alert').first().fadeIn(300);
    
    // Auto-hide after 5 seconds
    setTimeout(function() {
        $('.alert').first().fadeOut(300, function() {
            $(this).remove();
        });
    }, 5000);
}

// Save form data (for auto-save functionality)
function saveFormData(form) {
    const formData = form.serialize();
    const url = form.attr('action') || window.location.href;
    
    $.ajax({
        url: url,
        method: 'POST',
        data: formData,
        success: function(response) {
            console.log('Form data saved automatically');
        },
        error: function(xhr, status, error) {
            console.error('Auto-save failed:', error);
        }
    });
}

// AJAX form submission
function submitForm(form, callback) {
    const formData = new FormData(form[0]);
    const url = form.attr('action');
    const method = form.attr('method') || 'POST';
    
    $.ajax({
        url: url,
        method: method,
        data: formData,
        processData: false,
        contentType: false,
        beforeSend: function() {
            form.find('button[type="submit"]').prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> Processing...');
        },
        success: function(response) {
            if (callback) callback(null, response);
            showAlert('Operation completed successfully', 'success');
        },
        error: function(xhr, status, error) {
            if (callback) callback(error, null);
            showAlert('An error occurred: ' + error, 'error');
        },
        complete: function() {
            form.find('button[type="submit"]').prop('disabled', false).html('Submit');
        }
    });
}

// Delete confirmation
function confirmDelete(message, callback) {
    if (confirm(message || 'Are you sure you want to delete this item?')) {
        if (callback) callback();
    }
}

// Refresh page data
function refreshData() {
    location.reload();
}

// Export data functionality
function exportData(type, data) {
    const filename = `bmw_sis_${type}_${new Date().toISOString().split('T')[0]}.csv`;
    const csv = convertToCSV(data);
    downloadCSV(csv, filename);
}

// Convert data to CSV format
function convertToCSV(data) {
    if (!data || data.length === 0) return '';
    
    const headers = Object.keys(data[0]);
    const csvContent = [
        headers.join(','),
        ...data.map(row => headers.map(header => `"${row[header] || ''}"`).join(','))
    ].join('\n');
    
    return csvContent;
}

// Download CSV file
function downloadCSV(csv, filename) {
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    
    if (link.download !== undefined) {
        const url = URL.createObjectURL(blob);
        link.setAttribute('href', url);
        link.setAttribute('download', filename);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }
}

// Print functionality
function printTable(tableSelector) {
    const printWindow = window.open('', '_blank');
    const table = $(tableSelector).clone();
    
    printWindow.document.write(`
        <html>
        <head>
            <title>BMW SIS Report</title>
            <style>
                body { font-family: Arial, sans-serif; }
                table { width: 100%; border-collapse: collapse; }
                th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                th { background-color: #f2f2f2; }
                @media print { body { margin: 0; } }
            </style>
        </head>
        <body>
            <h1>BMW Student Information System</h1>
            <h2>Report Generated: ${new Date().toLocaleDateString()}</h2>
            ${table[0].outerHTML}
        </body>
        </html>
    `);
    
    printWindow.document.close();
    printWindow.print();
}

// Dashboard specific functions
function refreshDashboard() {
    showAlert('Refreshing dashboard...', 'info');
    setTimeout(function() {
        location.reload();
    }, 1000);
}

function updateStats() {
    $.ajax({
        url: '/admin/api/stats',
        method: 'GET',
        success: function(data) {
            $('.stat-value').each(function() {
                const statType = $(this).data('stat');
                if (data[statType] !== undefined) {
                    $(this).text(data[statType]);
                }
            });
        },
        error: function() {
            console.log('Failed to update statistics');
        }
    });
}

// Mobile menu toggle
function toggleMobileMenu() {
    $('.sidebar').toggleClass('active');
    $('.main-content').toggleClass('sidebar-active');
}

// Initialize mobile responsiveness
$(window).on('resize', function() {
    if ($(window).width() > 768) {
        $('.sidebar').removeClass('active');
        $('.main-content').removeClass('sidebar-active');
    }
});

// Add loading state to buttons
$(document).on('click', 'button[data-loading]', function() {
    const button = $(this);
    const originalText = button.html();
    
    button.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> Loading...');
    
    setTimeout(function() {
        button.prop('disabled', false).html(originalText);
    }, 2000);
});

// Initialize page-specific functionality
function initializePageSpecific() {
    const currentPage = window.location.pathname.split('/').pop();
    
    switch(currentPage) {
        case 'dashboard':
            initializeDashboard();
            break;
        case 'students':
            initializeStudentManagement();
            break;
        case 'faculty':
            initializeFacultyManagement();
            break;
        case 'subjects':
            initializeSubjectManagement();
            break;
        case 'schedules':
            initializeScheduleManagement();
            break;
        default:
            console.log('No specific initialization for page:', currentPage);
    }
}

// Page-specific initializations
function initializeDashboard() {
    console.log('Dashboard specific initialization');
    // Add any dashboard-specific functionality here
}

function initializeStudentManagement() {
    console.log('Student management initialization');
    // Add student management specific functionality
}

function initializeFacultyManagement() {
    console.log('Faculty management initialization');
    // Add faculty management specific functionality
}

function initializeSubjectManagement() {
    console.log('Subject management initialization');
    // Add subject management specific functionality
}

function initializeScheduleManagement() {
    console.log('Schedule management initialization');
    // Add schedule management specific functionality
}

// Call page-specific initialization
$(document).ready(function() {
    initializePageSpecific();
});

// Global error handler
window.onerror = function(msg, url, lineNo, columnNo, error) {
    console.error('Global error:', msg, 'at', url, ':', lineNo);
    showAlert('An unexpected error occurred. Please refresh the page.', 'error');
    return false;
};

// Fix circular loading issues by ensuring proper initialization
function fixCircularLoadingIssues() {
    // Remove all loading circles and indicators
    $('.loading, .stat-card .loading, .stats-grid .loading').remove();
    
    // Ensure all stat cards are properly initialized
    $('.stat-card').removeClass('loading').addClass('loaded').css({
        'opacity': '1',
        'visibility': 'visible'
    });
    
    // Ensure all stat values are properly displayed
    $('.stat-value').each(function() {
        const $this = $(this);
        let value = $this.text().trim();
        
        // Handle empty or invalid values
        if (value === '' || value === 'undefined' || value === 'null' || isNaN(value)) {
            $this.text('0');
        } else {
            // Ensure it's a valid number
            const numValue = parseInt(value) || 0;
            $this.text(numValue);
        }
    });
    
    // Remove any circular CSS artifacts
    $('.stat-icon').css('border-radius', '12px');
}

// Call the fix function on document ready and after page load
$(document).ready(function() {
    setTimeout(fixCircularLoadingIssues, 200);
});

$(window).on('load', function() {
    fixCircularLoadingIssues();
}); 